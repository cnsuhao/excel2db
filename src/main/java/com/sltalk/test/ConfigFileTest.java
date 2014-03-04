package com.sltalk.test;

import com.sltalk.cfg.ConfigFile;
import com.sltalk.cfg.SheetConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by h_hu on 14-3-4.
 */
public class ConfigFileTest {

    private ConfigFile cfg = null;
    private String parseError = null;
    private SheetConfig testSheetCfg1 = null;
    private SheetConfig testSheetCfg2 = null;
    private SheetConfig testSheetCfg3 = null;
    private SheetConfig testSheetCfg4 = null;

    @Before
    public void Setup() {
        cfg = new ConfigFile();

        {
            Map<String, String> map = new HashMap();
            map.put("col1", "field11");
            map.put("col2", "field12");
            map.put("col3", null);

            testSheetCfg1 = new SheetConfig();
            testSheetCfg1.setName("sheet1");
            testSheetCfg1.setExport(true);
            testSheetCfg1.setTable("table1");
            testSheetCfg1.setPrimaryKey("col1");
            testSheetCfg1.setDrop(true);
            testSheetCfg1.setFieldMap(map);
        }

        {
            List<String> names = new ArrayList<String>();
            names.add("field21");
            names.add("field22");
            names.add(null);

            testSheetCfg2 = new SheetConfig();
            testSheetCfg2.setName("sheet2");
            testSheetCfg2.setExport(true);
            testSheetCfg2.setTable("table2");
            testSheetCfg2.setFieldNames(names);
        }

        {
            testSheetCfg3 = new SheetConfig();
            testSheetCfg3.setName("sheet3");
            testSheetCfg3.setExport(true);
        }

        {
            testSheetCfg4 = new SheetConfig();
            testSheetCfg4.setName("sheet4");
            testSheetCfg4.setExport(false);
        }

        try {
            cfg.parse("test/test.config");
        } catch (Exception e) {
            parseError = e.getMessage();
        }
    }

    @After
    public void Shutdown() {
        cfg = null;
        testSheetCfg1 = null;
        testSheetCfg3 = null;
        testSheetCfg2 = null;
        testSheetCfg4 = null;
    }

    @Test
    public void TestParse() {
        if(parseError != null)
            Assert.fail(parseError);
    }

    @Test
    public void TestCommon() {
        Assert.assertEquals(cfg.getSourceFile(), "test/test.xls");
    }

    @Test
    public void TestSchema() {
        Assert.assertEquals(cfg.isHeuristics(), true);
        Assert.assertEquals(cfg.getDefaultType(), "varchar(100)");
    }

    @Test
    public void TestExport() {
        // Dest
        Assert.assertEquals(cfg.getDestFile(), "test/test.sql");

        // Database
        Assert.assertEquals(cfg.getJdbcHost(), "localhost");
        Assert.assertEquals(cfg.getJdbcUser(), "root");
        Assert.assertEquals(cfg.getJdbcPassword(), "password");
        Assert.assertEquals(cfg.getJdbcDatabase(), "testDB");

        // Sheets
        List<SheetConfig> sheets = cfg.getSheetConfigs();
        Assert.assertNotNull(sheets);
        Assert.assertEquals(sheets.size(), 4);

        Assert.assertEquals(testSheetCfg1, sheets.get(0));
        Assert.assertEquals(testSheetCfg2, sheets.get(1));
        Assert.assertEquals(testSheetCfg3, sheets.get(2));
        Assert.assertEquals(testSheetCfg4, sheets.get(3));
    }
}
