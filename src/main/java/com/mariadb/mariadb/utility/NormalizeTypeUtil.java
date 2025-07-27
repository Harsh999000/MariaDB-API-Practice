package com.mariadb.mariadb.utility;

public class NormalizeTypeUtil {
    public static String normalizeType(String rawType) {
        // Default null = String
        if (rawType == null) {
            return "string";
        }

        rawType = rawType.toLowerCase();

        if ((rawType.contains("int"))) {
            return "int";
        }

        if (rawType.contains("double")) {
            return "double";
        }

        if (rawType.contains("float") || rawType.contains("decimal")) {
            return "float";
        }

        if (rawType.contains("bool")) {
            return "boolean";
        }

        if (rawType.contains("date") && rawType.contains("time")) {
            return "date";
        }

        if (rawType.contains("timestamp") || rawType.contains("datetime")) {
            return "datetime";
        }

        if (rawType.contains("time")) {
            return "time";
        }

        if (rawType.contains("char") || rawType.contains("text")) {
            return "string";
        }

        // fallback
        return "string";
    }
}
