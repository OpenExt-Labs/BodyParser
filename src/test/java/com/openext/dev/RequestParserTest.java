package com.openext.dev;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openext.dev.parser.RequestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class RequestParserTest {

    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseParamToList_StringList() {
        when(mockRequest.getParameter("tags")).thenReturn("java,spring,gradle");

        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "", false, String.class);
        System.err.println("result: " + result);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("java", result.get(0));
        assertEquals("spring", result.get(1));
        assertEquals("gradle", result.get(2));
    }

    @Test
    public void testParseParamToList_IntegerList() {
        when(mockRequest.getParameter("numbers")).thenReturn("1,2,3,4");

        List<Integer> result = RequestParser.parseParamToList(mockRequest, "numbers", "", false, Integer.class);

        System.err.println("result: " + result);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals(Integer.valueOf(2), result.get(1));
        assertEquals(Integer.valueOf(3), result.get(2));
        assertEquals(Integer.valueOf(4), result.get(3));
    }

    @Test
    public void testParseParamToList_EmptyString() {
        when(mockRequest.getParameter("tags")).thenReturn("");

        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "", false, String.class);
        System.err.println("result: " + result);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseParamToList_WithDefaultValue() {
        when(mockRequest.getParameter("tags")).thenReturn(null);

        List<String> result = RequestParser.parseParamToList(mockRequest, "tags", "java,python", false, String.class);
        System.err.println("result: " + result);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("java", result.get(0));
        assertEquals("python", result.get(1));
    }

    @Test
    public void testParseParamToList_Enum() {
        when(mockRequest.getParameter("statuses")).thenReturn("NEW,IN_PROGRESS");

        List<MyStatusEnum> result = RequestParser.parseParamToList(mockRequest, "statuses", "", false,
                MyStatusEnum.class);
        System.err.println("result: " + result);

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
        when(mockRequest.getParameter("values")).thenReturn("1,3.14,2.718");

        List<Double> result = RequestParser.parseParamToList(mockRequest, "values", "", true, Double.class);
        System.err.println("result: " + result);

        assertNotNull(result);
    }
}
