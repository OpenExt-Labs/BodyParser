package com.openext.dev.utils;


import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static long getLong(HttpServletRequest req, String paramName, long defaultValue, boolean isRequired) throws IllegalArgumentException {
        Long value = HReqParam.getLong(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static int getInt(HttpServletRequest req, String paramName, int defaultValue, boolean isRequired) throws IllegalArgumentException {
        Integer value = HReqParam.getInt(req, paramName, defaultValue);
        if (isRequired && value == defaultValue) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static String getString(HttpServletRequest req, String paramName, String defaultValue, boolean isRequired) throws IllegalArgumentException {
        String value = HReqParam.getString(req, paramName, defaultValue);
        if (isRequired && value.equals(defaultValue)) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static <E extends Enum<E>> E getEnum(HttpServletRequest req, String paramName, Class<E> enumClass, String defaultValue, boolean isRequired) throws IllegalArgumentException {
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