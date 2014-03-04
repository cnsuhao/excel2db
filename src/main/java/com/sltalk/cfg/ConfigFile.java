package com.sltalk.cfg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by h_hu on 14-3-4.
 *
 * Used to parse config file.
 */
public class ConfigFile {

    // Common
    private String sourceFile = "";

    // Schema
    private boolean heuristics = false;
    private String defaultType = "varchar(255)";
    private String schemaFile = "";

    // Export
    private String destFile = "";
    private List<SheetConfig> sheetConfigs = null;

    // Database
    private String jdbcHost = "";
    private String jdbcUser = "";
    private String jdbcPassword = "";
    private String jdbcDatabase = "";

    public void parse(String configFilePath) throws Exception {

        try {
            JSONObject root = JsonLoader.loadFromPath(configFilePath);
            if(root == null)
                throw new Exception("wrong format of config");

            // Common
            String error = parseCommon(root);
            if(error != null)
                throw new Exception(error);

            // Schema
            error = parseSchema(root);
            if(error != null)
                throw new Exception(error);

            // Export
            error = parseExport(root);
            if(error != null)
                throw new Exception(error);

        } catch (JSONException e) {
            e.printStackTrace(System.err);
            throw new Exception("format error in config file");
        }
    }

    private String parseCommon(JSONObject root){
        try {
            sourceFile = root.getString("source");
            return null;
        } catch (JSONException e) {
            return "no source file";
        }
    }

    private String parseSchema(JSONObject root) {
        if(!root.has("schema"))
            return null;

        JSONObject schema;
        try {
            schema = root.getJSONObject("schema");
            if(!schema.has("dest"))
                return "no dest file specified";

            // destFile
            destFile = schema.getString("dest");

            if(schema.has("heuristics"))
                heuristics = schema.getBoolean("heuristics");
            if(schema.has("defaultType"))
                defaultType = schema.getString("defaultType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseExport(JSONObject root) {
        if(!root.has("export"))
            return "no export config";

        try {
            JSONObject export = root.getJSONObject("export");

            // Database
            if(!export.has("database"))
                return "no database config";

            String error = parseDatabase(export);
            if(error != null)
                return error;

            // Sheets
            if(export.has("sheets")) {
                JSONArray sheets = export.getJSONArray("sheets");
                parseSheets(sheets);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseSheets(JSONArray sheets) {
        sheetConfigs = new ArrayList<SheetConfig>();
        try {
            int count = sheets.length();
            for(int i=0; i<count; ++ i) {
                JSONObject sheet = sheets.getJSONObject(i);
                SheetConfig cfgSheet = new SheetConfig();

                if(!sheet.has("sheet"))
                    continue;
                cfgSheet.setName(sheet.getString("sheet"));

                if(sheet.has("table"))
                    cfgSheet.setTable(sheet.getString("table"));

                if(sheet.has("export"))
                    cfgSheet.setExport(sheet.getBoolean("export"));
                if(!cfgSheet.isExport()) {
                    sheetConfigs.add(cfgSheet);
                    continue;
                }

                if(sheet.has("primary"))
                    cfgSheet.setPrimaryKey(sheet.getString("primary"));

                if(sheet.has("drop"))
                    cfgSheet.setDrop(sheet.getBoolean("drop"));

                if(sheet.has("field_maps")) {
                    cfgSheet.setFieldNames(null);
                    Map<String, String> map = new HashMap<String, String>();

                    JSONObject field_maps = sheet.getJSONObject("field_maps");
                    Iterator iterator = field_maps.keys();
                    while(iterator.hasNext()) {
                        String name = (String) iterator.next();
                        if(field_maps.isNull(name)) {
                            map.put(name, null);
                        } else {
                            String value = field_maps.getString(name);
                            map.put(name, value);
                        }
                    }
                    cfgSheet.setFieldMap(map);
                } else if(sheet.has("field_names")) {
                    cfgSheet.setFieldMap(null);
                    List<String> names = new ArrayList<String>();
                    JSONArray field_names = sheet.getJSONArray("field_names");
                    int nameCount = field_names.length();
                    for(int index=0; index<nameCount; ++ index) {
                        if(field_names.isNull(index))
                            names.add(null);
                        else
                            names.add(field_names.getString(index));
                    }
                    cfgSheet.setFieldNames(names);
                }

                sheetConfigs.add(cfgSheet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseDatabase(JSONObject export){
        try
        {
            // database
            JSONObject database = export.getJSONObject("database");

            // database
            if(!database.has("jdbc_database"))
                return "no jdbc_database found";

            jdbcDatabase = database.getString("jdbc_database");

            // host
            if(database.has("jdbc_host"))
                jdbcHost = database.getString("jdbc_host");
            else
                jdbcHost = "localhost";
            // user
            if(database.has("jdbc_user"))
                jdbcUser = database.getString("jdbc_user");
            else
                jdbcUser = "root";
            // password
            if(database.has("jdbc_password"))
                jdbcPassword = database.getString("jdbc_password");
            else
                jdbcPassword = "";
        }
        catch(JSONException e) {
        }
        return null;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public boolean isHeuristics() {
        return heuristics;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public String getDestFile() {
        return destFile;
    }

    public List<SheetConfig> getSheetConfigs() {
        return sheetConfigs;
    }

    public String getJdbcHost() {
        return jdbcHost;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getJdbcDatabase() {
        return jdbcDatabase;
    }

    public SheetConfig getSheetConfigByName(String name) {
        for(SheetConfig cfg : sheetConfigs) {
            if(cfg.getName().compareToIgnoreCase(name) == 0)
                return cfg;
        }
        return null;
    }

    public String getSchemaFile() {
        return schemaFile;
    }
}
