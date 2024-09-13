package com.openext.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openext.dev.annotations.RequestParam;

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
     * Hàm wrapper để phân tích dữ liệu từ HttpServletRequest và ánh xạ vào đối tượng tương ứng.
     *
     * @param request  Đối tượng HttpServletRequest.
     * @param clazz    Lớp mục tiêu để ánh xạ dữ liệu.
     * @param <T>      Kiểu đối tượng mong muốn.
     * @return Đối tượng được phân tích và ánh xạ từ dữ liệu request.
     * @throws IOException                Nếu có lỗi trong quá trình phân tích.
     * @throws IllegalAccessException     Nếu có lỗi trong quá trình ánh xạ dữ liệu.
     * @throws MissingParameterException  Nếu thiếu các tham số bắt buộc.
     */
    public <T> T parse(HttpServletRequest request, Class<T> clazz) throws IOException, IllegalAccessException, MissingParameterException {
        String contentType = request.getContentType();
        InputStream inputStream = request.getInputStream();
        return parse(inputStream, clazz, contentType);
    }

    /**
     * Phân tích dữ liệu từ InputStream và ánh xạ vào đối tượng tương ứng dựa trên annotation @RequestParam.
     *
     * @param inputStream Dữ liệu đầu vào dưới dạng InputStream.
     * @param clazz       Lớp mục tiêu để ánh xạ dữ liệu.
     * @param <T>         Kiểu đối tượng mong muốn.
     * @return Đối tượng được phân tích và ánh xạ từ dữ liệu request.
     * @throws IOException            Nếu có lỗi trong quá trình phân tích.
     * @throws IllegalAccessException Nếu có lỗi trong quá trình ánh xạ dữ liệu.
     * @throws MissingParameterException Nếu thiếu các tham số bắt buộc.
     */
    public <T> T parse(InputStream inputStream, Class<T> clazz, String contentType) throws IOException, IllegalAccessException, MissingParameterException {
        Map<String, String[]> parsedData;

        if (contentType.contains("application/json")) {
            parsedData = parseJson(inputStream);
        } else if (contentType.contains("application/x-www-form-urlencoded")) {
            parsedData = parseUrlEncoded(inputStream);
        } else {
            throw new UnsupportedOperationException("Unsupported Content-Type: " + contentType);
        }

        // Tạo một instance của lớp mục tiêu
        T instance = createInstance(clazz);

        // Lấy tất cả các trường của lớp
        Field[] fields = clazz.getDeclaredFields();

        List<String> missingParams = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = field.getAnnotation(RequestParam.class);
                String paramName = requestParam.name();
                boolean required = requestParam.required();
                String message = requestParam.message();

                String[] values = parsedData.get(paramName);
                if (values == null || values.length == 0 || (values.length == 1 && values[0].isEmpty())) {
                    if (required) {
                        if (!message.isEmpty()) {
                            missingParams.add(message);
                        } else {
                            missingParams.add(paramName);
                        }
                    }
                    continue;
                }

                field.setAccessible(true);
                Class<?> fieldType = field.getType();

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
                } else {
                    // Đối với các loại phức tạp hơn, bạn có thể sử dụng ObjectMapper để ánh xạ
                    String jsonValue = objectMapper.writeValueAsString(values[0]);
                    Object complexObject = objectMapper.readValue(jsonValue, fieldType);
                    field.set(instance, complexObject);
                }
            }
        }

        if (!missingParams.isEmpty()) {
            throw new MissingParameterException("Missing required parameters: " + String.join(", ", missingParams));
        }

        return instance;
    }

    /**
     * Phân tích dữ liệu JSON từ InputStream.
     *
     * @param inputStream Dữ liệu đầu vào dưới dạng InputStream.
     * @return Map chứa các cặp key-value từ dữ liệu JSON.
     * @throws IOException Nếu có lỗi trong quá trình phân tích.
     */
    private Map<String, String[]> parseJson(InputStream inputStream) throws IOException {
        // Sử dụng ObjectMapper để chuyển JSON thành Map
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

    /**
     * Phân tích dữ liệu URL-encoded từ InputStream.
     *
     * @param inputStream Dữ liệu đầu vào dưới dạng InputStream.
     * @return Map chứa các cặp key-value từ dữ liệu URL-encoded.
     * @throws IOException Nếu có lỗi trong quá trình đọc dữ liệu.
     */
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

        // Chuyển List<String> thành String[]
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : tempMap.entrySet()) {
            List<String> list = entry.getValue();
            result.put(entry.getKey(), list.toArray(new String[0]));
        }

        return result;
    }

    /**
     * Tạo một instance của lớp mục tiêu.
     *
     * @param clazz Lớp mục tiêu.
     * @param <T>   Kiểu đối tượng.
     * @return Một instance của lớp mục tiêu.
     */
    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of " + clazz.getName(), e);
        }
    }
}
