# Request Parsing Framework

Welcome to the **Request Parsing Framework**! This framework offers a streamlined approach to handling and validating HTTP requests in Java applications using custom annotations. By leveraging reflection and annotations, it simplifies the process of mapping request parameters to Java objects, supporting various data types, default values, and validation mechanisms.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Components](#components)
    - [Annotations](#annotations)
        - [`@RequestParam`](#requestparam)
    - [Exceptions](#exceptions)
        - [`MissingParameterException`](#missingparameterexception)
    - [Classes](#classes)
        - [`BodyParser`](#bodyparser)
        - [`RequestParser`](#requestparser)
- [Usage](#usage)
    - [Defining Data Classes](#defining-data-classes)
    - [Parsing Requests](#parsing-requests)
- [Testing](#testing)
- [Example](#example)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The **Request Parsing Framework** is designed to simplify the handling of HTTP request parameters in Java web applications. By using custom annotations and reflection, it automates the process of mapping request data to Java objects, enforcing validation rules, and applying default values where necessary. This reduces boilerplate code and enhances the maintainability of your application.

## Features

- **Custom Annotations**: Easily define request parameters with annotations.
- **Type Support**: Handle various data types, including primitives, wrappers, `String`, and collections like `List<String>`, `List<Integer>`, etc.
- **Default Values**: Assign default values to parameters when they are not provided in the request.
- **Validation**: Enforce required parameters and provide custom error messages.
- **JSON Parsing**: Convert request bodies to `JSONObject` for flexible data handling.
- **Unit Testing**: Comprehensive test cases to ensure reliability.

## Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/request-parsing-framework.git
   cd request-parsing-framework
   ```

2. **Add Dependencies**

   Ensure you have the following dependencies added to your `pom.xml` if you're using Maven:

   ```xml
   <dependencies>
       <!-- JSON Library -->
       <dependency>
           <groupId>org.json</groupId>
           <artifactId>json</artifactId>
           <version>20230618</version>
       </dependency>
       
       <!-- Jackson for JSON Processing -->
       <dependency>
           <groupId>com.fasterxml.jackson.core</groupId>
           <artifactId>jackson-databind</artifactId>
           <version>2.15.2</version>
       </dependency>
       
       <!-- JUnit 5 for Testing -->
       <dependency>
           <groupId>org.junit.jupiter</groupId>
           <artifactId>junit-jupiter</artifactId>
           <version>5.9.3</version>
           <scope>test</scope>
       </dependency>
       
       <!-- Mockito for Mocking -->
       <dependency>
           <groupId>org.mockito</groupId>
           <artifactId>mockito-core</artifactId>
           <version>5.3.1</version>
           <scope>test</scope>
       </dependency>
       
       <!-- Lombok for Getters and Setters -->
       <dependency>
           <groupId>org.projectlombok</groupId>
           <artifactId>lombok</artifactId>
           <version>1.18.28</version>
           <scope>provided</scope>
       </dependency>
   </dependencies>
   ```

   If you're not using Maven, download the necessary JAR files and add them to your project's classpath.

## Components

### Annotations

#### `@RequestParam`

The `@RequestParam` annotation is used to mark fields in your data classes that should be populated from HTTP request parameters.

**Attributes:**

- `name` (String): The name of the request parameter.
- `required` (boolean): Indicates if the parameter is mandatory. Default is `false`.
- `defaultValue` (String): The default value to assign if the parameter is not present. Default is an empty string.
- `message` (String): Custom error message when a required parameter is missing. Default is an empty string.

**Example:**

```java
package com.openext.dev.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequestParam {
    String name();
    boolean required() default false;
    String defaultValue() default "";
    String message() default "";
}
```

### Exceptions

#### `MissingParameterException`

The `MissingParameterException` is a custom exception that is thrown when a required request parameter is missing from the HTTP request.

**Definition:**

```java
package com.openext.dev.validation;

public class MissingParameterException extends Exception {
    public MissingParameterException(String message) {
        super(message);
    }
}
```

**Usage:**

```java
if (missingParams.isEmpty()) {
    throw new MissingParameterException("Missing required parameters: " + String.join(", ", missingParams));
}
```

### Classes

#### `BodyParser`

The `BodyParser` class is responsible for parsing HTTP request bodies and mapping them to Java objects based on the `@RequestParam` annotations. It supports JSON content types and can convert request bodies to `JSONObject`.

**Key Methods:**

- `parse(HttpServletRequest request, Class<T> clazz)`: Parses the request and populates an instance of the specified class.
- `parseToJSONObject(InputStream inputStream)`: Reads the request body and parses it into a `JSONObject`.

**Implementation:**

#### Usage Example

```java
import javax.servlet.http.HttpServletRequest;
import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "tags", required = false, defaultValue = "bac,java,python", message = "Tags are required")
    private List<String> tags;

    @RequestParam(name = "scores", required = false, defaultValue = "80,90,85", message = "Scores are required")
    private List<Integer> scores;

    @RequestParam(name = "ratings", required = false, defaultValue = "4.5,3.8,5.0", message = "Ratings are required")
    private List<Float> ratings;

    @RequestParam(name = "metrics", required = false, defaultValue = "0.75,0.85,0.95", message = "Metrics are required")
    private List<Double> metrics;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", tags=" + tags +
                ", scores=" + scores +
                ", ratings=" + ratings +
                ", metrics=" + metrics +
                '}';
    }
}
```

#### Parsing an HTTP Request

```java
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class ExampleUsage {
    private BodyParser bodyParser = new BodyParser();

    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object
            UserInfo userInfo = bodyParser.parse(request, UserInfo.class);
            System.out.println(userInfo);

            // Alternatively, parse the request body into a JSONObject
            JSONObject jsonObject = bodyParser.parseToJSONObject(request.getInputStream());
            System.out.println(jsonObject.toString(2)); // Pretty print JSON
        } catch (MissingParameterException | IOException | IllegalAccessException | JSONException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Output:**

```
UserInfo{name='Alice', age=25, tags=[bac, java, python], scores=[80, 90, 85], ratings=[4.5, 3.8, 5.0], metrics=[0.75, 0.85, 0.95]}
{
  "name": "Alice",
  "age": 25,
  "hobbies": [
    "reading",
    "swimming"
  ]
}
```

#### `RequestParser`

The `RequestParser` class provides static methods to parse HTTP request parameters into Java objects using the `@RequestParam` annotation. It offers additional flexibility and utility functions for handling different parameter types.

**Key Methods:**

- `parseRequest(HttpServletRequest req, Class<T> clazz)`: Parses the request parameters and maps them to an instance of the specified class.
- `parseParamToList(HttpServletRequest req, String paramName, String defaultValue, boolean isRequired, Class<T> listType)`: Parses a specific parameter into a `List` of the desired type.

**Implementation:**

#### Usage Example

```java
import javax.servlet.http.HttpServletRequest;
import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "tags", required = false, defaultValue = "bac,java,python", message = "Tags are required")
    private List<String> tags;

    @RequestParam(name = "scores", required = false, defaultValue = "80,90,85", message = "Scores are required")
    private List<Integer> scores;

    @RequestParam(name = "ratings", required = false, defaultValue = "4.5,3.8,5.0", message = "Ratings are required")
    private List<Float> ratings;

    @RequestParam(name = "metrics", required = false, defaultValue = "0.75,0.85,0.95", message = "Metrics are required")
    private List<Double> metrics;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", tags=" + tags +
                ", scores=" + scores +
                ", ratings=" + ratings +
                ", metrics=" + metrics +
                '}';
    }
}
```

#### Parsing an HTTP Request with `BodyParser`

```java
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class ExampleUsage {
    private BodyParser bodyParser = new BodyParser();

    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object
            UserInfo userInfo = bodyParser.parse(request, UserInfo.class);
            System.out.println(userInfo);

            // Alternatively, parse the request body into a JSONObject
            JSONObject jsonObject = bodyParser.parseToJSONObject(request.getInputStream());
            System.out.println(jsonObject.toString(2)); // Pretty print JSON
        } catch (MissingParameterException | IOException | IllegalAccessException | JSONException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Output:**

```
UserInfo{name='Alice', age=25, tags=[bac, java, python], scores=[80, 90, 85], ratings=[4.5, 3.8, 5.0], metrics=[0.75, 0.85, 0.95]}
{
  "name": "Alice",
  "age": 25,
  "hobbies": [
    "reading",
    "swimming"
  ]
}
```

#### Parsing an HTTP Request with `RequestParser`

```java
import javax.servlet.http.HttpServletRequest;

public class ExampleUsageWithRequestParser {
    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object using RequestParser
            UserInfo userInfo = RequestParser.parseRequest(request, UserInfo.class);
            System.out.println(userInfo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Output:**

```
UserInfo{name='Alice', age=25, tags=[bac, java, python], scores=[80, 90, 85], ratings=[4.5, 3.8, 5.0], metrics=[0.75, 0.85, 0.95]}
```

## Usage

### Defining Data Classes

Define your data classes with fields annotated using `@RequestParam` to specify how they should be populated from HTTP request parameters.

**Example:**

```java
import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "tags", required = false, defaultValue = "bac,java,python", message = "Tags are required")
    private List<String> tags;

    @RequestParam(name = "scores", required = false, defaultValue = "80,90,85", message = "Scores are required")
    private List<Integer> scores;

    @RequestParam(name = "ratings", required = false, defaultValue = "4.5,3.8,5.0", message = "Ratings are required")
    private List<Float> ratings;

    @RequestParam(name = "metrics", required = false, defaultValue = "0.75,0.85,0.95", message = "Metrics are required")
    private List<Double> metrics;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", tags=" + tags +
                ", scores=" + scores +
                ", ratings=" + ratings +
                ", metrics=" + metrics +
                '}';
    }
}
```

### Parsing Requests

Use the `BodyParser` or `RequestParser` class to parse incoming HTTP requests and map the parameters to your data classes.

**Using `BodyParser`:**

```java
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class ExampleUsage {
    private BodyParser bodyParser = new BodyParser();

    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object
            UserInfo userInfo = bodyParser.parse(request, UserInfo.class);
            System.out.println(userInfo);

            // Alternatively, parse the request body into a JSONObject
            JSONObject jsonObject = bodyParser.parseToJSONObject(request.getInputStream());
            System.out.println(jsonObject.toString(2)); // Pretty print JSON
        } catch (MissingParameterException | IOException | IllegalAccessException | JSONException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Using `RequestParser`:**

```java
import javax.servlet.http.HttpServletRequest;

public class ExampleUsageWithRequestParser {
    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object using RequestParser
            UserInfo userInfo = RequestParser.parseRequest(request, UserInfo.class);
            System.out.println(userInfo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

## Testing

The framework includes comprehensive unit tests to ensure reliability and correctness. Tests are written using JUnit 5 and Mockito.

### Running Tests

1. **Ensure Dependencies Are Added**

   Make sure you have JUnit 5 and Mockito included in your project dependencies as shown in the [Installation](#installation) section.

2. **Execute Tests**

   Run the tests using your IDE's built-in test runner or via Maven:

   ```bash
   mvn test
   ```

### Example Test Case for `BodyParser.parseToJSONObject`
]()
```java
package com.openext.dev.parser;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BodyParserTest {
    private HttpServletRequest mockRequest;
    private BodyParser bodyParser;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        bodyParser = new BodyParser();
    }

    @Test
    public void testParseBodyToJSONObject() throws IOException, JSONException {
        // 1. Mock Content-Type
        when(mockRequest.getContentType()).thenReturn("application/json");

        // 2. Mock JSON data
        String jsonData = "{\"name\": \"Alice\", \"age\": 25, \"hobbies\": [\"reading\", \"swimming\"]}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

        // 3. Create a ServletInputStream from ByteArrayInputStream
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
                // Not needed for unit testing
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };

        // 4. Mock getInputStream to return the ServletInputStream
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        // 5. Call parseToJSONObject method
        JSONObject jsonObject = bodyParser.parseToJSONObject(mockRequest.getInputStream());

        // 6. Assert the results
        assertNotNull(jsonObject, "JSONObject should not be null");

        // Validate fields
        assertEquals("Alice", jsonObject.getString("name"), "Name should be 'Alice'");
        assertEquals(25, jsonObject.getInt("age"), "Age should be 25");

        // Validate hobbies array
        assertTrue(jsonObject.has("hobbies"), "Hobbies should be present");
        assertTrue(jsonObject.get("hobbies") instanceof org.json.JSONArray, "Hobbies should be a JSONArray");

        org.json.JSONArray hobbies = jsonObject.getJSONArray("hobbies");
        assertEquals(2, hobbies.length(), "Hobbies should contain 2 items");
        assertEquals("reading", hobbies.getString(0), "First hobby should be 'reading'");
        assertEquals("swimming", hobbies.getString(1), "Second hobby should be 'swimming'");
    }
}
```

### Example Test Case for `BodyParser.parse`

```java
package com.openext.dev.parser;

import com.openext.dev.annotations.RequestParam;
import com.openext.dev.validation.MissingParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BodyParserTest {
    private HttpServletRequest mockRequest;
    private BodyParser bodyParser;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        bodyParser = new BodyParser();
    }

    @Test
    public void testParseWithAllFieldsFromRequest() throws IOException, IllegalAccessException, MissingParameterException {
        // 1. Mock Content-Type
        when(mockRequest.getContentType()).thenReturn("application/json");

        // 2. Mock JSON data
        String jsonData = "{\"name\": \"Alice\", \"age\": 25, \"tags\": [\"developer\", \"backend\", \"java\"], \"scores\": [80, 90, 85], \"ratings\": [4.5, 3.8, 5.0], \"metrics\": [0.75, 0.85, 0.95]}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

        // 3. Create a ServletInputStream from ByteArrayInputStream
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
            public void setReadListener(javax.servlet.ReadListener readListener) {
                // Not needed for unit testing
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };

        // 4. Mock getInputStream to return the ServletInputStream
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        // 5. Call parse method
        UserInfo userInfo = bodyParser.parse(mockRequest, UserInfo.class);

        // 6. Assert the results
        assertNotNull(userInfo, "UserInfo should not be null");
        assertEquals("Alice", userInfo.getName(), "Name should be 'Alice'");
        assertEquals(25, userInfo.getAge(), "Age should be 25");

        assertNotNull(userInfo.getTags(), "Tags should not be null");
        assertEquals(3, userInfo.getTags().size(), "Tags should contain 3 items");
        assertTrue(userInfo.getTags().containsAll(List.of("developer", "backend", "java")), "Tags should contain 'developer', 'backend', 'java'");

        assertNotNull(userInfo.getScores(), "Scores should not be null");
        assertEquals(3, userInfo.getScores().size(), "Scores should contain 3 items");
        assertTrue(userInfo.getScores().containsAll(List.of(80, 90, 85)), "Scores should contain 80, 90, 85");

        assertNotNull(userInfo.getRatings(), "Ratings should not be null");
        assertEquals(3, userInfo.getRatings().size(), "Ratings should contain 3 items");
        assertTrue(userInfo.getRatings().containsAll(List.of(4.5f, 3.8f, 5.0f)), "Ratings should contain 4.5, 3.8, 5.0");

        assertNotNull(userInfo.getMetrics(), "Metrics should not be null");
        assertEquals(3, userInfo.getMetrics().size(), "Metrics should contain 3 items");
        assertTrue(userInfo.getMetrics().containsAll(List.of(0.75, 0.85, 0.95)), "Metrics should contain 0.75, 0.85, 0.95");
    }

    @Test
    public void testParseWithDefaultValues() throws IOException, IllegalAccessException, MissingParameterException {
        // 1. Mock Content-Type
        when(mockRequest.getContentType()).thenReturn("application/json");

        // 2. Mock JSON data with missing optional fields
        String jsonData = "{\"name\": \"Alice\", \"age\": 25}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

        // 3. Create a ServletInputStream from ByteArrayInputStream
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
            public void setReadListener(javax.servlet.ReadListener readListener) {
                // Not needed for unit testing
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };

        // 4. Mock getInputStream to return the ServletInputStream
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        // 5. Call parse method
        UserInfo userInfo = bodyParser.parse(mockRequest, UserInfo.class);

        // 6. Assert the results
        assertNotNull(userInfo, "UserInfo should not be null");
        assertEquals("Alice", userInfo.getName(), "Name should be 'Alice'");
        assertEquals(25, userInfo.getAge(), "Age should be 25");

        // Check default values
        assertNotNull(userInfo.getTags(), "Tags should not be null");
        assertEquals(List.of("bac", "java", "python"), userInfo.getTags(), "Tags should contain default values");

        assertNotNull(userInfo.getScores(), "Scores should not be null");
        assertEquals(List.of(80, 90, 85), userInfo.getScores(), "Scores should contain default values");

        assertNotNull(userInfo.getRatings(), "Ratings should not be null");
        assertEquals(List.of(4.5f, 3.8f, 5.0f), userInfo.getRatings(), "Ratings should contain default values");

        assertNotNull(userInfo.getMetrics(), "Metrics should not be null");
        assertEquals(List.of(0.75, 0.85, 0.95), userInfo.getMetrics(), "Metrics should contain default values");
    }

    @Test
    public void testParseMissingRequiredParameter() {
        // 1. Mock Content-Type
        when(mockRequest.getContentType()).thenReturn("application/json");

        // 2. Mock JSON data missing required field 'age'
        String jsonData = "{\"name\": \"Alice\"}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

        // 3. Create a ServletInputStream from ByteArrayInputStream
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
                // Not needed for unit testing
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };

        // 4. Mock getInputStream to return the ServletInputStream
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        // 5. Call parse method and expect MissingParameterException
        MissingParameterException exception = assertThrows(
            MissingParameterException.class,
            () -> bodyParser.parse(mockRequest, UserInfo.class),
            "Expected parse() to throw MissingParameterException due to missing 'age'"
        );

        // 6. Assert the exception message
        assertTrue(exception.getMessage().contains("age"), "Exception message should mention missing 'age'");
    }
}
```

#### Example Test Case for `RequestParser.parseRequest`

```java
package com.openext.dev.parser;

import com.openext.dev.annotations.RequestParam;
import com.openext.dev.validation.MissingParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RequestParserTest {
    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseRequestWithAllFields() throws UnsupportedEncodingException {
        // 1. Mock request parameters
        when(mockRequest.getParameter("name")).thenReturn("Alice");
        when(mockRequest.getParameter("age")).thenReturn("25");
        when(mockRequest.getParameter("tags")).thenReturn("developer,backend,java");
        when(mockRequest.getParameter("scores")).thenReturn("80,90,85");
        when(mockRequest.getParameter("ratings")).thenReturn("4.5,3.8,5.0");
        when(mockRequest.getParameter("metrics")).thenReturn("0.75,0.85,0.95");

        // 2. Call parseRequest method
        UserInfo userInfo = RequestParser.parseRequest(mockRequest, UserInfo.class);

        // 3. Assert the results
        assertNotNull(userInfo, "UserInfo should not be null");
        assertEquals("Alice", userInfo.getName(), "Name should be 'Alice'");
        assertEquals(25, userInfo.getAge(), "Age should be 25");

        assertNotNull(userInfo.getTags(), "Tags should not be null");
        assertEquals(3, userInfo.getTags().size(), "Tags should contain 3 items");
        assertTrue(userInfo.getTags().containsAll(List.of("developer", "backend", "java")), "Tags should contain 'developer', 'backend', 'java'");

        assertNotNull(userInfo.getScores(), "Scores should not be null");
        assertEquals(3, userInfo.getScores().size(), "Scores should contain 3 items");
        assertTrue(userInfo.getScores().containsAll(List.of(80, 90, 85)), "Scores should contain 80, 90, 85");

        assertNotNull(userInfo.getRatings(), "Ratings should not be null");
        assertEquals(3, userInfo.getRatings().size(), "Ratings should contain 3 items");
        assertTrue(userInfo.getRatings().containsAll(List.of(4.5f, 3.8f, 5.0f)), "Ratings should contain 4.5, 3.8, 5.0");

        assertNotNull(userInfo.getMetrics(), "Metrics should not be null");
        assertEquals(3, userInfo.getMetrics().size(), "Metrics should contain 3 items");
        assertTrue(userInfo.getMetrics().containsAll(List.of(0.75, 0.85, 0.95)), "Metrics should contain 0.75, 0.85, 0.95");
    }

    @Test
    public void testParseRequestWithDefaultValues() throws UnsupportedEncodingException {
        // 1. Mock request parameters with missing optional fields
        when(mockRequest.getParameter("name")).thenReturn("Alice");
        when(mockRequest.getParameter("age")).thenReturn("25");
        when(mockRequest.getParameter("tags")).thenReturn(null);
        when(mockRequest.getParameter("scores")).thenReturn(null);
        when(mockRequest.getParameter("ratings")).thenReturn(null);
        when(mockRequest.getParameter("metrics")).thenReturn(null);

        // 2. Call parseRequest method
        UserInfo userInfo = RequestParser.parseRequest(mockRequest, UserInfo.class);

        // 3. Assert the results
        assertNotNull(userInfo, "UserInfo should not be null");
        assertEquals("Alice", userInfo.getName(), "Name should be 'Alice'");
        assertEquals(25, userInfo.getAge(), "Age should be 25");

        // Check default values
        assertNotNull(userInfo.getTags(), "Tags should not be null");
        assertEquals(List.of("bac", "java", "python"), userInfo.getTags(), "Tags should contain default values");

        assertNotNull(userInfo.getScores(), "Scores should not be null");
        assertEquals(List.of(80, 90, 85), userInfo.getScores(), "Scores should contain default values");

        assertNotNull(userInfo.getRatings(), "Ratings should not be null");
        assertEquals(List.of(4.5f, 3.8f, 5.0f), userInfo.getRatings(), "Ratings should contain default values");

        assertNotNull(userInfo.getMetrics(), "Metrics should not be null");
        assertEquals(List.of(0.75, 0.85, 0.95), userInfo.getMetrics(), "Metrics should contain default values");
    }

    @Test
    public void testParseRequestMissingRequiredParameter() {
        // 1. Mock request parameters missing required field 'age'
        when(mockRequest.getParameter("name")).thenReturn("Alice");
        when(mockRequest.getParameter("age")).thenReturn(null);

        // 2. Call parseRequest method and expect IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> RequestParser.parseRequest(mockRequest, UserInfo.class),
            "Expected parseRequest() to throw IllegalArgumentException due to missing 'age'"
        );

        // 3. Assert the exception message
        assertTrue(exception.getMessage().contains("age"), "Exception message should mention missing 'age'");
    }
}
```

## Example

### Defining the `@RequestParam` Annotation

```java
package com.openext.dev.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequestParam {
    String name();
    boolean required() default false;
    String defaultValue() default "";
    String message() default "";
}
```

### Defining the `UserInfo` Class

```java
package com.openext.dev;

import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "tags", required = false, defaultValue = "bac,java,python", message = "Tags are required")
    private List<String> tags;

    @RequestParam(name = "scores", required = false, defaultValue = "80,90,85", message = "Scores are required")
    private List<Integer> scores;

    @RequestParam(name = "ratings", required = false, defaultValue = "4.5,3.8,5.0", message = "Ratings are required")
    private List<Float> ratings;

    @RequestParam(name = "metrics", required = false, defaultValue = "0.75,0.85,0.95", message = "Metrics are required")
    private List<Double> metrics;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", tags=" + tags +
                ", scores=" + scores +
                ", ratings=" + ratings +
                ", metrics=" + metrics +
                '}';
    }
}
```

### Parsing an HTTP Request with `BodyParser`

```java
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class ExampleUsage {
    private BodyParser bodyParser = new BodyParser();

    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object
            UserInfo userInfo = bodyParser.parse(request, UserInfo.class);
            System.out.println(userInfo);

            // Alternatively, parse the request body into a JSONObject
            JSONObject jsonObject = bodyParser.parseToJSONObject(request.getInputStream());
            System.out.println(jsonObject.toString(2)); // Pretty print JSON
        } catch (MissingParameterException | IOException | IllegalAccessException | JSONException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Output:**

```
UserInfo{name='Alice', age=25, tags=[bac, java, python], scores=[80, 90, 85], ratings=[4.5, 3.8, 5.0], metrics=[0.75, 0.85, 0.95]}
{
  "name": "Alice",
  "age": 25,
  "hobbies": [
    "reading",
    "swimming"
  ]
}
```

### Parsing an HTTP Request with `RequestParser`

```java
import javax.servlet.http.HttpServletRequest;

public class ExampleUsageWithRequestParser {
    public void handleRequest(HttpServletRequest request) {
        try {
            // Parse request parameters into UserInfo object using RequestParser
            UserInfo userInfo = RequestParser.parseRequest(request, UserInfo.class);
            System.out.println(userInfo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
```

**Output:**

```
UserInfo{name='Alice', age=25, tags=[bac, java, python], scores=[80, 90, 85], ratings=[4.5, 3.8, 5.0], metrics=[0.75, 0.85, 0.95]}
```

## Contributing

We welcome contributions to the **Request Parsing Framework**! Please follow these steps to contribute:

1. **Fork the Repository**

2. **Create a Feature Branch**

   ```bash
   git checkout -b feature/YourFeature
   ```

3. **Commit Your Changes**

   ```bash
   git commit -m "Add your feature"
   ```

4. **Push to the Branch**

   ```bash
   git push origin feature/YourFeature
   ```

5. **Open a Pull Request**

   Provide a clear description of your changes and the problem they solve.

## License

This project is licensed under the [MIT License](LICENSE).

---

*For any questions or issues, please open an issue on the repository or contact the maintainer.*