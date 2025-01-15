package org.game.config;

public class ConfigException extends Exception {

    private String tableName;

    private Object id;

    private String fieldName;

    public ConfigException(String tableName, Object id, String fieldName, String message) {
        super(message);
        this.tableName = tableName;
        this.id = id;
        this.fieldName = fieldName;
    }

    public String getTableName() {
        return tableName;
    }

    public Object getId() {
        return id;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return String.format("表: %s, id: %s, 字段: %s, 说明: %s", tableName, id.toString(), fieldName, getMessage());
    }
}
