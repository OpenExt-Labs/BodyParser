package com.openext.dev.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.openext.dev.annotations.RequestParam;
import com.openext.dev.utils.RequestUtils;

public class RequestParser {

    public static <T> T parseRequest(HttpServletRequest req, Class<T> clazz) throws IllegalArgumentException {
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = field.getAnnotation(RequestParam.class);
                    String paramName = requestParam.name();
                    boolean isRequired = requestParam.required();
                    String defaultValue = requestParam.defaultValue();
                    String customMessage = requestParam.message(); // Add custom message here

                    field.setAccessible(true);

                    Object value = null;
                    try {
                        // Check if the field is a List type
                        if (List.class.isAssignableFrom(field.getType())) {
                            // Handle List<String>
                            Type genericType = field.getGenericType();
                            if (genericType instanceof ParameterizedType) {
                                ParameterizedType pt = (ParameterizedType) genericType;
                                Class<?> listType = (Class<?>) pt.getActualTypeArguments()[0];
                                value = parseParamToList(req, paramName, defaultValue, isRequired, listType);
                            }
                        } else if (field.getType() == String.class) {
                            value = RequestUtils.getString(req, paramName, defaultValue, isRequired);
                        } else if (field.getType() == Integer.class) {
                            value = RequestUtils.getInt(req, paramName,
                                    defaultValue.isEmpty() ? null : Integer.parseInt(defaultValue), isRequired);
                        } else if (field.getType() == int.class) {
                            value = RequestUtils.getInt(req, paramName,
                                    defaultValue.isEmpty() ? 0 : Integer.parseInt(defaultValue), isRequired);
                        } else if (field.getType() == Long.class) {
                            value = RequestUtils.getLong(req, paramName,
                                    defaultValue.isEmpty() ? null : Long.parseLong(defaultValue), isRequired);
                        } else if (field.getType().isEnum()) {
                            value = RequestUtils.getEnum(req, paramName, (Class<? extends Enum>) field.getType(),
                                    defaultValue, isRequired);
                        } else if (field.getType() == Boolean.class) {
                            value = RequestUtils.getBoolean(req, paramName,
                                    defaultValue.isEmpty() ? null : Boolean.parseBoolean(defaultValue), isRequired);
                        } else if (field.getType() == Double.class) {
                            value = RequestUtils.getDouble(req, paramName,
                                    defaultValue.isEmpty() ? null : Double.parseDouble(defaultValue), isRequired);
                        } else if (field.getType() == Float.class) {
                            value = RequestUtils.getFloat(req, paramName,
                                    defaultValue.isEmpty() ? null : Float.parseFloat(defaultValue), isRequired);
                        } else {
                            throw new IllegalArgumentException("Unsupported field type: " + field.getType().getName());
                        }
                    } catch (IllegalArgumentException ex) {
                        // Use custom message if provided, otherwise default message
                        String errorMessage = !customMessage.isEmpty() ? customMessage
                                : "Invalid value for parameter: " + paramName;
                        throw new IllegalArgumentException(errorMessage, ex);
                    }

                    field.set(instance, value);
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error parsing request parameters: " + ex.getMessage(), ex);
        }
        return instance;
    }

    // New method to parse a parameter to a list
    public static List<String> parseParamToList(HttpServletRequest req, String paramName, String defaultValue,
            boolean isRequired) {
        String paramValue = RequestUtils.getString(req, paramName, defaultValue, isRequired);
        if (paramValue != null && !paramValue.isEmpty()) {
            // Split the paramValue by comma and convert it into a list
            return Arrays.asList(paramValue.split(","));
        }
        return Collections.emptyList(); // Return empty list if param is null or empty
    }

    // Hàm parseParamToList nhận vào kiểu dữ liệu của List
    public static <T> List<T> parseParamToList(HttpServletRequest req, String paramName, String defaultValue,
            boolean isRequired, Class<T> listType) {
        String paramValue = RequestUtils.getString(req, paramName, defaultValue, isRequired);
        if (paramValue == null || paramValue.isEmpty()) {
            return Collections.emptyList();
        }

        String[] values = paramValue.split(",");

        List<T> result = new ArrayList<>();
        for (String value : values) {
            result.add(convertToType(value.trim(), listType));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertToType(String value, Class<T> type) {
        if (type == String.class) {
            return (T) value;
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (type == Long.class) {
            return (T) Long.valueOf(value);
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(value);
        } else if (type == Double.class) {
            return (T) Double.valueOf(value);
        } else if (type == Float.class) {
            return (T) Float.valueOf(value);
        } else if (type.isEnum()) {
            return (T) Enum.valueOf((Class<Enum>) type, value.toUpperCase());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
    }

    public static void main(String[] args) {
        Integer value = null;

        System.err.println("Value: " + value);
    }
}
