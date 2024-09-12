package com.openext.dev.utils;

import javax.servlet.http.HttpServletRequest;

public class HReqParam {

    public HReqParam() {
    }

    private static String _getParameterAsString(HttpServletRequest req, String paramName) {
        if (paramName == null) {
            throw new IllegalArgumentException("Parameter name is null");
        }

        paramName = paramName.trim();
        if (paramName.isEmpty()) {
            throw new IllegalArgumentException("Parameter name is empty");
        }

        String strVal = req.getParameter(paramName);
        if (strVal == null) {
            throw new NullPointerException("Parameter " + paramName + " does not exist");
        }

        return strVal;
    }

    private static String _getParameterAsTrimString(HttpServletRequest req, String paramName) {
        return _getParameterAsString(req, paramName).trim();
    }

    public static Boolean getBoolean(HttpServletRequest req, String paramName) {
        String strVal = _getParameterAsString(req, paramName);
        return Boolean.parseBoolean(strVal);
    }

    public static Byte getByte(HttpServletRequest req, String paramName) {
        return Byte.parseByte(_getParameterAsTrimString(req, paramName));
    }

    public static Double getDouble(HttpServletRequest req, String paramName) {
        return Double.parseDouble(_getParameterAsTrimString(req, paramName));
    }

    public static Float getFloat(HttpServletRequest req, String paramName) {
        return Float.parseFloat(_getParameterAsTrimString(req, paramName));
    }

    public static Integer getInt(HttpServletRequest req, String paramName) {
        return Integer.parseInt(_getParameterAsTrimString(req, paramName));
    }

    public static Long getLong(HttpServletRequest req, String paramName) {
        return Long.parseLong(_getParameterAsTrimString(req, paramName));
    }

    public static Short getShort(HttpServletRequest req, String paramName) {
        return Short.parseShort(_getParameterAsTrimString(req, paramName));
    }

    public static String getString(HttpServletRequest req, String paramName) {
        return _getParameterAsString(req, paramName);
    }

    public static Boolean getBoolean(HttpServletRequest req, String paramName, Boolean defaultVal) {
        try {
            return getBoolean(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Byte getByte(HttpServletRequest req, String paramName, Byte defaultVal) {
        try {
            return getByte(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Double getDouble(HttpServletRequest req, String paramName, Double defaultVal) {
        try {
            return getDouble(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Float getFloat(HttpServletRequest req, String paramName, Float defaultVal) {
        try {
            return getFloat(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Integer getInt(HttpServletRequest req, String paramName, Integer defaultVal) {
        try {
            return getInt(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Long getLong(HttpServletRequest req, String paramName, Long defaultVal) {
        try {
            return getLong(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Short getShort(HttpServletRequest req, String paramName, Short defaultVal) {
        try {
            return getShort(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static String getString(HttpServletRequest req, String paramName, String defaultVal) {
        try {
            return getString(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static Boolean getBooleanExl(HttpServletRequest req, String paramName) {
        return getBoolean(req, paramName, null);
    }

    public static Byte getByteExl(HttpServletRequest req, String paramName) {
        return getByte(req, paramName, null);
    }

    public static Double getDoubleExl(HttpServletRequest req, String paramName) {
        return getDouble(req, paramName, null);
    }

    public static Float getFloatExl(HttpServletRequest req, String paramName) {
        return getFloat(req, paramName, null);
    }

    public static Integer getIntExl(HttpServletRequest req, String paramName) {
        return getInt(req, paramName, null);
    }

    public static Long getLongExl(HttpServletRequest req, String paramName) {
        return getLong(req, paramName, null);
    }

    public static Short getShortExl(HttpServletRequest req, String paramName) {
        return getShort(req, paramName, null);
    }

    public static String getStringExl(HttpServletRequest req, String paramName) {
        return getString(req, paramName, null);
    }
}
