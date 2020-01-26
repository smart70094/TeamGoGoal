package com.teamgogoal.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty() || "null".equalsIgnoreCase(value);
    }

    public static boolean hasAssignment(String value) {
        return !isNullOrEmpty(value);
    }
}
