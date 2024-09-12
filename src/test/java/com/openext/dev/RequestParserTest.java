package com.openext.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;

public class RequestParserTest {

    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        // Create a mock HttpServletRequest using Mockito
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseParamToList_StringList() {
        // Simulate the request parameter "tags" with a string of comma-separated values
        when(mockRequest.getParameter("tags")).thenReturn("java,spring,gradle");

        // Call the method and check the result
        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "", false, String.class);
        System.err.println("result: " + result);

        // Assert that the result is a list of strings
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("java", result.get(0));
        assertEquals("spring", result.get(1));
        assertEquals("gradle", result.get(2));
    }

    @Test
    public void testParseParamToList_IntegerList() {
        // Simulate the request parameter "numbers" with a string of comma-separated integers
        when(mockRequest.getParameter("numbers")).thenReturn("1,2,3,4");

        // Call the method and check the result
        List<Integer> result = RequestParser.parseParamToList(mockRequest, "numbers", "", false, Integer.class);

        System.err.println("result: " + result);

        // Assert that the result is a list of integers
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals(Integer.valueOf(2), result.get(1));
        assertEquals(Integer.valueOf(3), result.get(2));
        assertEquals(Integer.valueOf(4), result.get(3));
    }

    @Test
    public void testParseParamToList_EmptyString() {
        // Simulate the request parameter "tags" with an empty string
        when(mockRequest.getParameter("tags")).thenReturn("");

        // Call the method and check the result
        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "", false, String.class);
        System.err.println("result: " + result);

        // Assert that the result is an empty list
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseParamToList_WithDefaultValue() {
        // Simulate the request parameter "tags" with a null value
        when(mockRequest.getParameter("tags")).thenReturn(null);

        // Call the method with a default value
        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "java,python", false, String.class);
        System.err.println("result: " + result);

        // Assert that the result contains the default values
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("java", result.get(0));
        assertEquals("python", result.get(1));
    }

    @Test
    public void testParseParamToList_Enum() {
        // Simulate the request parameter "statuses" with a string of comma-separated values for an enum
        when(mockRequest.getParameter("statuses")).thenReturn("NEW,IN_PROGRESS");

        // Call the method with an enum type
        List<MyStatusEnum> result = RequestParser.parseParamToList(mockRequest, "statuses", "", false, MyStatusEnum.class);
        System.err.println("result: " + result);

        // Assert that the result contains the expected enum values
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(MyStatusEnum.NEW, result.get(0));
        assertEquals(MyStatusEnum.IN_PROGRESS, result.get(1));
    }

    // Define a sample enum for testing purposes
    public enum MyStatusEnum {
        NEW, IN_PROGRESS, COMPLETED
    }

    @Test
    public void testParseParamToList_Double() {
        // Simulate the request parameter "values" with a string of comma-separated values
        when(mockRequest.getParameter("values")).thenReturn("1,2,3.5");

        // Call the method with an unsupported type
        try {
            List<Double> result = RequestParser.parseParamToList(mockRequest, "values", "", false, Double.class);
            System.err.println("result: " + result);
        } catch (IllegalArgumentException e) {
            System.err.println("Exception message: " + e.getMessage());
            // Check that the exception message contains the unsupported type
            assertTrue(e.getMessage().contains("Unsupported type: java.lang.Double"));
        }
    }
}
