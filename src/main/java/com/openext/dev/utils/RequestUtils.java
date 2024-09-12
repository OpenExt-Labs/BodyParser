package com.openext.dev.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static Long getLong(HttpServletRequest req, String paramName, Long defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        Long value = HReqParam.getLong(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static Integer getInt(HttpServletRequest req, String paramName, Integer defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        Integer value = HReqParam.getInt(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static String getString(HttpServletRequest req, String paramName, String defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        String value = HReqParam.getString(req, paramName, defaultValue);
        if (isRequired && value.equals(defaultValue)) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static boolean getBoolean(HttpServletRequest req, String paramName, Boolean defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        Boolean value = HReqParam.getBoolean(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static double getDouble(HttpServletRequest req, String paramName, Double defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        Double value = HReqParam.getDouble(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static float getFloat(HttpServletRequest req, String paramName, Float defaultValue, boolean isRequired)
            throws IllegalArgumentException {
        Float value = HReqParam.getFloat(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static <E extends Enum<E>> E getEnum(HttpServletRequest req, String paramName, Class<E> enumClass,
            String defaultValue, boolean isRequired) throws IllegalArgumentException {
        String valueStr = HReqParam.getString(req, paramName, defaultValue);
        if (valueStr == null || valueStr.isEmpty()) {
            if (isRequired) {
                throw new IllegalArgumentException(paramName + " is required");
            }
            return null;
        }
        try {
            return Enum.valueOf(enumClass, valueStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid value for parameter " + paramName + ": " + valueStr, ex);
        }
    }
}