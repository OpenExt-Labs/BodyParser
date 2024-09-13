package com.openext.dev.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.openext.dev.annotations.RequestParam;
import com.openext.dev.utils.RequestUtils;

public class RequestParser {

    /**
     * Parse request parameters to an object
     * @param req The HttpServletRequest object
     * @param clazz The class of the object to parse the request parameters to
     * @return The object with the request parameters set
     * @param <T> The type of the object
     * @throws IllegalArgumentException If there is an error parsing the request parameters
     */
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
                    String customMessage = requestParam.message();

                    field.setAccessible(true);

                    Object value = null;
                    try {
                        if (List.class.isAssignableFrom(field.getType())) {
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

    /***
     * Parse a request parameter to a list of a specific type
     * @param req The HttpServletRequest object
     * @param paramName The name of the request parameter
     * @param defaultValue The default value for the parameter
     * @param isRequired Whether the parameter is required
     * @param listType The type of the list
     * @param <T> The type of the list
     * @return The list of values for the parameter
     */
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
}
