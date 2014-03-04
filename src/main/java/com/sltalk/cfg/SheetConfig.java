package com.sltalk.cfg;

import com.sun.corba.se.spi.activation._InitialNameServiceImplBase;

import java.util.List;
import java.util.Map;

/**
 * Created by h_hu on 14-3-4.
 */
public class SheetConfig {

    private String name = "";
    private boolean export = true;
    private String table = "";
    private String primaryKey = "";
    private boolean drop = false;

    private Map<String, String> fieldMap = null;
    private List<String> fieldNames = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public Map<String, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, String> fieldMap) {
        if(fieldMap == null || fieldMap.size() == 0)
            this.fieldMap = null;
        else
            this.fieldMap = fieldMap;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0)
            this.fieldNames = null;
        else
            this.fieldNames = fieldNames;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(!(obj instanceof SheetConfig))
            return false;

        SheetConfig cfgRhs = (SheetConfig)obj;
        if(!this.name.equals(cfgRhs.getName())
                || this.export != cfgRhs.isExport()
                || !this.table.equals(cfgRhs.getTable())
                || !this.primaryKey.equals(cfgRhs.getPrimaryKey())
                || this.drop != cfgRhs.isDrop()) {
            return false;
        }

        if((this.fieldMap == null && cfgRhs.getFieldMap() != null)
            || (this.fieldMap != null && cfgRhs.getFieldMap() == null))
            return false;

        if(this.fieldMap != null && !this.fieldMap.equals(cfgRhs.getFieldMap()))
            return false;

        if((this.fieldNames == null && cfgRhs.getFieldNames() != null)
            || (this.fieldNames != null && cfgRhs.getFieldNames() == null))
            return false;

        if(this.fieldNames != null && !this.fieldNames.equals(cfgRhs.getFieldNames()))
            return false;

        return true;
    }
}
