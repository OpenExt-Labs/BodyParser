package com.openext.dev.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openext.dev.annotations.RequestParam;
import com.openext.dev.validation.MissingParameterException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BodyParser {
    private ObjectMapper objectMapper;

    public BodyParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parse the request body and map it to an instance of the specified class
     * @param request The HttpServletRequest object
     * @param clazz The class of the object to parse the request body to
     * @return The object with the request body set
     * @param <T> The type of the object
     * @throws IOException If there is an error reading the request body
     * @throws IllegalAccessException If there is an error setting the field value
     * @throws MissingParameterException If a required parameter is missing
     */
    public <T> T parse(HttpServletRequest request, Class<T> clazz) throws IOException, IllegalAccessException, MissingParameterException {
        String contentType = request.getContentType();
        InputStream inputStream = request.getInputStream();
        return parse(inputStream, clazz, contentType);
    }

    private <T> T parse(InputStream inputStream, Class<T> clazz, String contentType) throws IOException, IllegalAccessException, MissingParameterException {
        Map<String, String[]> parsedData;

        if (contentType.contains("application/json")) {
            parsedData = parseJson(inputStream);
        } else {
            throw new UnsupportedOperationException("Unsupported Content-Type: " + contentType);
        }

        T instance = createInstance(clazz);

        Field[] fields = clazz.getDeclaredFields();

        List<String> missingParams = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = field.getAnnotation(RequestParam.class);
                String paramName = requestParam.name();
                boolean required = requestParam.required();
                String message = requestParam.message();
                String defaultValue = requestParam.defaultValue();

                String[] values = parsedData.get(paramName);
                if (values == null || values.length == 0 || (values.length == 1 && values[0].isEmpty())) {
                    if (required) {
                        if (!message.isEmpty()) {
                            missingParams.add(message);
                        } else {
                            missingParams.add(paramName);
                        }
                    }
                    if (!required && !defaultValue.isEmpty()) {
                        if (List.class.isAssignableFrom(field.getType())) {
                            List<String> list = Arrays.asList(defaultValue.split(","));
                            field.setAccessible(true);
                            field.set(instance, list);
                        } else {
                            Object parsedDefaultValue = parseValue(defaultValue, field.getType(), paramName);
                            field.setAccessible(true);
                            field.set(instance, parsedDefaultValue);
                        }
                    }
                    continue;
                }

                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                try {
                    if (List.class.isAssignableFrom(fieldType)) {
                        List<String> list = Arrays.asList(values);
                        field.set(instance, list);
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        field.set(instance, Integer.parseInt(values[0]));
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        field.set(instance, Long.parseLong(values[0]));
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        field.set(instance, Double.parseDouble(values[0]));
                    } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                        field.set(instance, Boolean.parseBoolean(values[0]));
                    } else if (fieldType == String.class) {
                        field.set(instance, values[0]);
                    } else if (fieldType.isEnum()) {
                        field.set(instance, Enum.valueOf((Class<Enum>) fieldType, values[0].toUpperCase()));
                    } else {
                        String jsonValue = objectMapper.writeValueAsString(values[0]);
                        Object complexObject = objectMapper.readValue(jsonValue, fieldType);
                        field.set(instance, complexObject);
                    }
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Invalid value for parameter: " + paramName, ex);
                }
            }
        }


        if (!missingParams.isEmpty()) {
            throw new MissingParameterException("Missing required parameters: " + String.join(", ", missingParams));
        }

        return instance;
    }

    private Object parseValue(String value, Class<?> type, String paramName) {
        try {
            if (type == int.class || type == Integer.class) {
                return Integer.parseInt(value);
            } else if (type == long.class || type == Long.class) {
                return Long.parseLong(value);
            } else if (type == double.class || type == Double.class) {
                return Double.parseDouble(value);
            } else if (type == boolean.class || type == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (type == String.class) {
                return value;
            } else if (type.isEnum()) {
                return Enum.valueOf((Class<Enum>) type, value.toUpperCase());
            } else {
                String jsonValue = objectMapper.writeValueAsString(value);
                return objectMapper.readValue(jsonValue, type);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid default value for parameter: " + paramName, ex);
        }
    }

    private Map<String, String[]> parseJson(InputStream inputStream) throws IOException {
        Map<String, Object> tempMap = objectMapper.readValue(inputStream, Map.class);
        Map<String, String[]> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                String[] array = list.stream().map(Object::toString).toArray(String[]::new);
                result.put(entry.getKey(), array);
            } else {
                result.put(entry.getKey(), new String[]{value.toString()});
            }
        }

        return result;
    }

    private Map<String, String[]> parseUrlEncoded(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String[] pairs = sb.toString().split("&");
        Map<String, List<String>> tempMap = new HashMap<>();

        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], String.valueOf(StandardCharsets.UTF_8));
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], String.valueOf(StandardCharsets.UTF_8)) : "";
            tempMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : tempMap.entrySet()) {
            List<String> list = entry.getValue();
            result.put(entry.getKey(), list.toArray(new String[0]));
        }

        return result;
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of " + clazz.getName(), e);
        }
    }
}
