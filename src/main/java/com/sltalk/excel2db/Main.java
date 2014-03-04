package com.sltalk.excel2db;

import com.sltalk.biz.ExcelSchemaCreator;
import com.sltalk.cfg.ConfigFile;

/**
 * Created by dot on 3/3/14.
 */
public class Main {
    static public void main(String[] args) {
        if(args.length == 0) {
            showHelp();
            return;
        }

        boolean result = false;

        try {
            if(args[0].compareToIgnoreCase("-version") == 0)
                result = showVersion();
            else if(args.length == 2 && args[0].compareToIgnoreCase("-createSchema") == 0)
                result = exportSchema(args);
            else if(args.length == 2 && args[0].compareToIgnoreCase("-export") == 0)
                result = exportData(args);
        } catch (Exception e) {
            result = false;
            System.err.println(e.getMessage());
        }

        if(!result)
            showHelp();
    }

    static private boolean exportSchema(String[] args) throws Exception {
        String configFilePath = parseConfigFile(args);
        if(configFilePath == null) {
            System.err.println("no config file found");
            return false;
        }

        ConfigFile file = new ConfigFile();
        file.parse(configFilePath);

        ExcelSchemaCreator creator = new ExcelSchemaCreator();
        return creator.exportSchema(file);
    }

    static private boolean exportData(String[] args) {
        return false;
    }

    static private void showHelp() {
        String help =
                "Usage: excel2db <verb>\r\n" +
                "\r\n" +
                "Verb: \r\n" +
                "-help                      Show help information\r\n" +
                "-version                   Show version information\r\n" +
                "-createSchema -config<config file> Create schema file according to format of excel files, according to config file\r\n" +
                "-export -config<config file>  Export data of excel files to database, according to config file\r\n" +
                "\r\n" ;
        System.out.println(help);
    }

    static private boolean showVersion() {
        String version = "excel2db "
                + InfoBundle.MajorVersion + "."
                + InfoBundle.MinorVersion + "."
                + InfoBundle.SubVersion;
        System.out.println(version);
        return true;
    }

    static private String parseConfigFile(String[] args) throws Exception {
        String configFile = null;
        for(String arg : args) {
            if(arg.startsWith("-config")) {
                configFile = arg.substring(7);
                return configFile;
            }
        }
        throw new Exception("no config file found");
    }
}
