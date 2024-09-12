package com.openext.dev;

import com.openext.dev.annotations.RequestParam;
import com.openext.dev.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

import java.lang.reflect.*;
import java.util.*;

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
                    String customMessage = requestParam.message();  // Add custom message here

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

                                if (listType == String.class) {
                                    String paramValue = RequestUtils.getString(req, paramName, defaultValue, isRequired);
                                    if (paramValue != null && !paramValue.isEmpty()) {
                                        // Split the string and convert it to a list
                                        value = Arrays.asList(paramValue.split(","));
                                    } else {
                                        value = Collections.emptyList(); // Return empty list if no values
                                    }
                                } else {
                                    throw new IllegalArgumentException("Unsupported List type: " + listType.getName());
                                }
                            }
                        } else if (field.getType() == String.class) {
                            value = RequestUtils.getString(req, paramName, defaultValue, isRequired);
                        } else if (field.getType() == int.class) {
                            value = RequestUtils.getInt(req, paramName, defaultValue.isEmpty() ? 0 : Integer.parseInt(defaultValue), isRequired);
                        } else if (field.getType() == long.class) {
                            value = RequestUtils.getLong(req, paramName, defaultValue.isEmpty() ? 0L : Long.parseLong(defaultValue), isRequired);
                        } else if (field.getType().isEnum()) {
                            value = RequestUtils.getEnum(req, paramName, (Class<? extends Enum>) field.getType(), defaultValue, isRequired);
                        } else {
                            throw new IllegalArgumentException("Unsupported field type: " + field.getType().getName());
                        }
                    } catch (IllegalArgumentException ex) {
                        // Use custom message if provided, otherwise default message
                        String errorMessage = !customMessage.isEmpty() ? customMessage : "Invalid value for parameter: " + paramName;
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
    public static List<String> parseParamToList(HttpServletRequest req, String paramName, String defaultValue, boolean isRequired) {
        String paramValue = RequestUtils.getString(req, paramName, defaultValue, isRequired);
        if (paramValue != null && !paramValue.isEmpty()) {
            // Split the paramValue by comma and convert it into a list
            return Arrays.asList(paramValue.split(","));
        }
        return Collections.emptyList(); // Return empty list if param is null or empty
    }

    // Hàm parseParamToList nhận vào kiểu dữ liệu của List
    public static <T> List<T> parseParamToList(HttpServletRequest req, String paramName, String defaultValue, boolean isRequired, Class<T> listType) {
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
        } else if (type.isEnum()) {
            return (T) Enum.valueOf((Class<Enum>) type, value.toUpperCase());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
    }
}