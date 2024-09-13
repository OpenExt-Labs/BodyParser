package com.openext.dev;

import com.openext.dev.entity.UserInfo;
import com.openext.dev.parser.RequestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
