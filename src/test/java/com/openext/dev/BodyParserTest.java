package com.openext.dev;

import com.openext.dev.entity.UserInfo2;
import com.openext.dev.parser.BodyParser;
import com.openext.dev.validation.MissingParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BodyParserTest {
    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseJsonRequest() throws MissingParameterException, IOException, IllegalAccessException {
        // 1. Mô phỏng Content-Type
        when(mockRequest.getContentType()).thenReturn("application/json");

        // 2. Tạo dữ liệu giả lập cho JSON
        String jsonData = "{\"age\": 25, \"hobbies\": [\"reading\", \"swimming\"], \"favoriteNumbers\": [1, 3, 3]}";

        // 3. Mô phỏng ServletInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };

        // 4. Mô phỏng getInputStream để trả về dữ liệu giả lập
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        // 5. Khởi tạo BodyParser và thực hiện phân tích
        BodyParser bodyParser = new BodyParser();
        UserInfo2 userInfo = bodyParser.parse(mockRequest, UserInfo2.class);
        System.err.println(userInfo);

        // 6. Sử dụng assertions để xác minh kết quả
        assertNotNull(userInfo, "UserInfo should not be null");
        assertEquals("Alice", userInfo.getName(), "Name should be 'Alice'");
        assertEquals(25, userInfo.getAge(), "Age should be 25");
        assertNotNull(userInfo.getHobbies(), "Hobbies should not be null");
        assertEquals(2, userInfo.getHobbies().size(), "Hobbies should contain 2 items");
        assertTrue(userInfo.getHobbies().containsAll(Arrays.asList("reading", "swimming")),
                "Hobbies should contain 'reading' and 'swimming'");
    }

    @Test
    public void testParseUrlEncodedRequest() {
        // 1. Mô phỏng Content-Type
        when(mockRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");

        // 2. Mô phỏng dữ liệu giả lập cho form-urlencoded
        when(mockRequest.getParameter("name")).thenReturn("Alice");
        when(mockRequest.getParameter("age")).thenReturn("25");
        when(mockRequest.getParameter("hobbies")).thenReturn("reading,swimming");

        // 3. Khởi tạo BodyParser và thực hiện phân tích
        BodyParser bodyParser = new BodyParser();

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    UserInfo2 userInfo = bodyParser.parse(mockRequest, UserInfo2.class);
                },
                "Unsupported type"

        );
        System.err.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Unsupported Content-Type"));
    }
}
