excel2db
========

utility used to import data from excel into db, like mysql, etc.


Usage
========

excel2db <verb>

```sh
Verb: 
                             -help                      Show help information
                             -version                   Show version information
                             -createSchema -source<excel file> [-dest<sql file>] [-heuristic] create schema file according to format of excel files
                                                          -source excel file to export
                                                          -dest where sql file is stored
                                                          -heuristic used only with -createSchema, go through all rows to find the most suitable data type
                             -run -config<config file>  export data of excel files to database, according to config file
```
