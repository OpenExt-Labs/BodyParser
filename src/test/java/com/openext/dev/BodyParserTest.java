package com.openext.dev;

import com.openext.dev.entity.UserInfo2;
import com.openext.dev.parser.BodyParser;
import com.openext.dev.validation.MissingParameterException;
import org.json.JSONException;
import org.json.JSONObject;
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
    private BodyParser bodyParser;


    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        bodyParser = new BodyParser();
    }

    @Test
    public void testParseJsonRequest() throws MissingParameterException, IOException, IllegalAccessException {
        when(mockRequest.getContentType()).thenReturn("application/json");

        String jsonData = "{\"age\": 25, \"hobbies\": [\"reading\", \"swimming\"], \"favoriteNumbers\": [1, 3, 3]}";

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

        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        BodyParser bodyParser = new BodyParser();
        UserInfo2 userInfo = bodyParser.parse(mockRequest, UserInfo2.class);
        System.err.println(userInfo);

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
        when(mockRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");

        when(mockRequest.getParameter("name")).thenReturn("Alice");
        when(mockRequest.getParameter("age")).thenReturn("25");
        when(mockRequest.getParameter("hobbies")).thenReturn("reading,swimming");


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

    @Test
    public void testParseBodyToJSONObject() throws IOException, JSONException {
        when(mockRequest.getContentType()).thenReturn("application/json");

        String jsonData = "{\"name\": \"Alice\", \"age\": 25, \"hobbies\": [\"reading\", \"swimming\"]}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

        ServletInputStream servletInputStream = new ServletInputStream() {
            private final ByteArrayInputStream bis = byteArrayInputStream;

            @Override
            public boolean isFinished() {
                return bis.available() == 0;
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
                return bis.read();
            }
        };

        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        JSONObject jsonObject = bodyParser.parseToJSONObject(mockRequest.getInputStream());

        System.err.println(jsonObject);

        assertNotNull(jsonObject, "JSONObject should not be null");

        assertEquals("Alice", jsonObject.getString("name"), "Name should be 'Alice'");
        assertEquals(25, jsonObject.getInt("age"), "Age should be 25");

        assertTrue(jsonObject.has("hobbies"), "Hobbies should be present");
        assertTrue(jsonObject.get("hobbies") instanceof org.json.JSONArray, "Hobbies should be a JSONArray");

        org.json.JSONArray hobbies = jsonObject.getJSONArray("hobbies");
        assertEquals(2, hobbies.length(), "Hobbies should contain 2 items");
        assertEquals("reading", hobbies.getString(0), "First hobby should be 'reading'");
        assertEquals("swimming", hobbies.getString(1), "Second hobby should be 'swimming'");
    }
}
