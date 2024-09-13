package com.openext.dev;

import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Getter
@Setter
class UserInfo2 {
    @RequestParam(name = "name", defaultValue = "Hello", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "hobbies", required = true, message = "Hobbies are required")
    private List<String> hobbies;

    @RequestParam(name="favoriteNumbers", defaultValue = "1,3,3")
    private List<Integer> favoriteNumbers;

    @Override
    public String toString() {
        return "UserInfo2{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", favoriteNumbers=" + favoriteNumbers +
                '}';
    }
}

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
        String jsonData = "{\"age\": 25, \"hobbies\": [\"reading\", \"swimming\"], \"favoriteNumbers\": [1, 2, 3]}";

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
        assertTrue(userInfo.getHobbies().containsAll(Arrays.asList("reading", "swimming")), "Hobbies should contain 'reading' and 'swimming'");
    }
}
