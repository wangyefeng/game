package org.game.config;

public class ConfigException extends Exception {

    private final String tableName;

    private final Object id;

    private final String fieldName;

    public ConfigException(String tableName, Object id, String fieldName, Throwable cause) {
        super(format(tableName, id, fieldName, cause.getMessage()), cause);
        this.tableName = tableName;
        this.id = id;
        this.fieldName = fieldName;
    }

    public ConfigException(String tableName, Object id, Throwable cause) {
        this(tableName, id, null, cause);
    }

    public ConfigException(String tableName, Throwable cause) {
        this(tableName, null, null, cause);
    }

    public ConfigException(String tableName, Object id, String fieldName, String message) {
        super(format(tableName, id, fieldName, message));
        this.tableName = tableName;
        this.id = id;
        this.fieldName = fieldName;
    }

    public ConfigException(String tableName, Object id, String message) {
        this(tableName, id, null, message);
    }

    public ConfigException(String tableName, String message) {
        this(tableName, null, null, message);
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

    private static String format(String tableName, Object id, String fieldName, String message) {
        if (id == null) {
            return String.format("表: %s, 原因: %s", tableName, message);
        }
        if (fieldName == null) {
            return String.format("表: %s, id: %s, 原因: %s", tableName, id, message);
        }
        return String.format("表: %s, id: %s, 字段: %s, 原因: %s", tableName, id, fieldName, message);
    }
}
