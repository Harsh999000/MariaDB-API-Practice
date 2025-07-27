package com.mariadb.mariadb.model;

// Import to preserve sequence in json
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "field", "type", "null", "key", "default", "Extra" })
public class TableColumnInfo {
    String Field;
    String Type;
    String Null;
    String Key;
    String Default;
    String Extra;

    // Getter for every object
    public String getField() {
        return Field;
    }

    public String getType() {
        return Type;
    }

    public String getNull() {
        return Null;
    }

    public String getKey() {
        return Key;
    }

    public String getDefault() {
        return Default;
    }

    public String getExtra() {
        return Extra;
    }

    // Setter for every object
    public void setField(String field) {
        this.Field = field;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public void setNull(String null1) {
        this.Null = null1;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public void setDefault(String default1) {
        this.Default = default1;
    }

    public void setExtra(String extra) {
        this.Extra = extra;
    }
}