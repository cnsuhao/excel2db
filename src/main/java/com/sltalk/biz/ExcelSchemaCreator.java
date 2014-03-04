package com.sltalk.biz;

import com.sltalk.cfg.ConfigFile;
import com.sltalk.cfg.SheetConfig;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.read.biff.PasswordException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by h_hu on 14-3-4.
 *
 * Used to export schema of excels.
 */
public class ExcelSchemaCreator {

    private ConfigFile cfgFile = null;
    private SheetConfig cfgSheet = null;
    private FileOutputStream schemaStream = null;

    public boolean exportSchema(ConfigFile cfgFile) throws Exception {

        boolean result = false;
        Workbook wb = null;
        try {
            this.cfgFile = cfgFile;
            this.cfgSheet = null;

            // open output file
            schemaStream = new FileOutputStream(new File(cfgFile.getDestFile()));

            File sourceFile = new File(cfgFile.getSourceFile());
            wb = Workbook.getWorkbook(sourceFile);

            int sheetCount = wb.getNumberOfSheets();
            if(sheetCount == 0) {
                wb.close();
                return true;
            }

            for(int i=0; i<sheetCount; ++ i) {
                Sheet sheet = wb.getSheet(i);
                cfgSheet = cfgFile.getSheetConfigByName(sheet.getName());
                if(cfgSheet == null)
                    throw new Exception("sheet config not found: " + sheet.getName());

                if(!cfgSheet.isExport())
                    continue;

                if(cfgSheet.isDrop())
                    appendSchema("DROP TABLE IF NOT EXISTS " + cfgSheet.getName() + ";");

                String error = exportSheetSchema(cfgFile, sheet);
                if(error != null)
                    throw new Exception(error);
            }

            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {
            if(wb != null)
                wb.close();
            if(schemaStream != null)
                schemaStream.close();
        }
        return result;
    }

    private class SheetNode {
        public String name = "";
        public String type = "";
        public int index = -1;
    }

    private String exportSheetSchema(ConfigFile cfgFile, Sheet sheet) {
        Map<String, SheetNode> mapNames = new HashMap<String, SheetNode>();

        List<String> fieldNames = cfgSheet.getFieldNames();
        if(fieldNames != null) {
            int nameCount = fieldNames.size();
            for(int i=0; i<nameCount; ++ i) {
                String name = fieldNames.get(i);
                if(name == null)
                    continue;
                SheetNode node = new SheetNode();
                node.name = name;
                node.index = i;
                mapNames.put(name, node);
            }
        } else {
            // read sheet's label
            int columnCount = sheet.getColumns();
            for(int i=0; i<columnCount; ++ i) {
                String label = sheet.getCell(i, 0).getContents();
                SheetNode node = new SheetNode();
                node.name = label;
                node.index = i;
                mapNames.put(label, node);
            }

            // refill names with cfgSheet
            Map<String, String> fieldMaps = cfgSheet.getFieldMap();
            if(fieldMaps != null) {
                Set<String> keys = fieldMaps.keySet();
                for(String key : keys) {
                    if(mapNames.containsKey(key)) {
                        SheetNode node = mapNames.get(key);
                        String obj = fieldMaps.get(key);
                        if(obj == null)
                            mapNames.remove(key);
                        else
                            node.name = obj;
                    }
                }
            }
        }


        if(cfgFile.isHeuristics())
            heuristicsType(mapNames, sheet);

        String schema = "CREATE TABLE IF NOT EXISTS " + cfgSheet.getName() + "(";
        Set<String> nameKeys = mapNames.keySet();
        for(String key : nameKeys) {
            SheetNode node = mapNames.get(key);
            schema += key + " ";
            schema += node.type;
            schema += ",";
        }

        String primaryKey = cfgSheet.getPrimaryKey();
        if(primaryKey == null || primaryKey.length() == 0)
            schema = schema.substring(0, schema.length() - 1);
        else
            schema += "primary key(" + primaryKey + ")";
        schema += ");";

        appendSchema(schema);

        return null;
    }

    private void appendSchema(String schema) {
        try {
            schemaStream.write(schema.getBytes());
            schemaStream.write('\r');
            schemaStream.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DbType
    {
        public String typeName = null;
        public int typeLength = -1;
        public CellType cellType = CellType.EMPTY;
    }

    private void heuristicsType(Map<String, SheetNode> mapNames, Sheet sheet) {

        int rows = sheet.getRows() - 1;

        int indexCount = 5 < rows ? 5 : rows;
        int[] rowIndex = new int[indexCount];

        for(int i=1; i<indexCount; ++ i) {
            rowIndex[i - 1] = rows * i / indexCount;
        }

        Set<String> keys = mapNames.keySet();
        for( String key : keys ) {
            SheetNode node = mapNames.get(key);

            DbType lastType = null;
            for(int i=0; i<indexCount; ++ i) {
                Cell cell = sheet.getCell(node.index, 1);
                DbType type = CellTypeToDbType(cell);
                if(type == null || type.typeName == null)
                    continue;;

                if(lastType == null) {
                    lastType = type;
                    continue;
                }

                if(!lastType.typeName.equals(type.typeName)) {
                    break;
                }
                if(lastType.cellType == CellType.LABEL)
                    lastType.typeLength = lastType.typeLength > type.typeLength
                            ? lastType.typeLength : type.typeLength;

                lastType = type;
            }
            if(lastType == null)
                node.type = cfgFile.getDefaultType();
            else if(lastType.cellType == CellType.LABEL)
                node.type = "varchar(" + lastType.typeLength + ")";
            else
                node.type = lastType.typeName;
        }
    }

    private DbType CellTypeToDbType(Cell cell) {
        DbType dbType = new DbType();
        CellType type = cell.getType();
        dbType.cellType = type;

        if(jxl.CellType.NUMBER == type) {
            String data = cell.getContents();
            if(data.contains("."))
                dbType.typeName = "double";
            else
                dbType.typeName = "int";
        } else if(jxl.CellType.BOOLEAN == type) {
            dbType.typeName = "int";
        } else if(jxl.CellType.NUMBER_FORMULA == type
                    || jxl.CellType.DATE_FORMULA == type
                    || jxl.CellType.STRING_FORMULA == type
                    || jxl.CellType.BOOLEAN_FORMULA == type
                    || jxl.CellType.FORMULA_ERROR == type) {
            dbType.typeName = null;
        } else if(jxl.CellType.DATE == type) {
            dbType.typeName = "timestamp";
        } else if(jxl.CellType.EMPTY == type) {
            dbType.typeName = null;
        } else if(jxl.CellType.LABEL == type) {
            String data = cell.getContents();
            dbType.typeName = "varchar";
            dbType.typeLength = data.length();
        } else if(jxl.CellType.ERROR == type) {
            dbType.typeName = null;
        }
        return dbType;
    }
}
