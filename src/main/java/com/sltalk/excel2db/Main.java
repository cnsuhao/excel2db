package com.sltalk.excel2db;

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
        if(args[0].compareToIgnoreCase("-version") == 0)
            result = showVersion();
        else if(args[0].compareToIgnoreCase("-createSchema") == 0)
            result = exportSchema(args[0]);
        else if(args[0].compareToIgnoreCase("-run") == 0 && args.length == 2)
            result = exportData(args[1]);

        if(!result)
            showHelp();
    }

    static boolean exportSchema(String arg) {
        return false;
    }

    static boolean exportData(String arg) {
        return false;
    }

    static void showHelp() {
        String help =
                "usage excel2db <verb>\r\n" +
                "\r\n" +
                "Verb: \r\n" +
                "-help                      Show help information\r\n" +
                "-version                   Show version information\r\n" +
                "-createSchema<schema file> create schema file according to format of excel files\r\n" +
                "-run -config<config file>  export data of excel files to database, according to config file\r\n" +
                "\r\n" ;
        System.out.println(help);
    }

    static boolean showVersion() {
        String version = "excel2db "
                + InfoBundle.MajorVersion + "."
                + InfoBundle.MinorVersion + "."
                + InfoBundle.SubVersion;
        System.out.println(version);
        return true;
    }
}
