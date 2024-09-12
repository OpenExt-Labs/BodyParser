package com.openext.dev;
import com.openext.dev.annotations.RequestParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
@Setter
class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;
    @RequestParam(name = "age", required = true)
    private int age;
    @RequestParam(name = "hobbies", required = true)
    private List<String> hobbies;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                '}';
    }
}

public class ParseEntityTest {
    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseRequest() {
        // simulate xxx-form-urlencoded request
        Mockito.when(mockRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");

        Mockito.when(mockRequest.getParameter("name")).thenReturn("Alice");
        Mockito.when(mockRequest.getParameter("age")).thenReturn("25");
        Mockito.when(mockRequest.getParameter("hobbies")).thenReturn("reading,swimming");

        UserInfo userInfo = RequestParser.parseRequest(mockRequest, UserInfo.class);
        System.err.println(userInfo);
        assertTrue(userInfo != null);
    }
}
