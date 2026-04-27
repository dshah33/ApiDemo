package com.api.utils;

import io.restassured.response.Response;
import org.testng.Assert;

public class Assertions {

    public static void assertStatusCode(Response response, HttpStatus expectedStatus) {
        Assert.assertEquals(response.getStatusCode(), expectedStatus.getCode(),
                "Expected status " + expectedStatus.getCode() + " " + expectedStatus.getDescription());
    }

    public static void assert200OK(Response response) {
        assertStatusCode(response, HttpStatus.OK);
    }

    public static void assert201Created(Response response) {
        assertStatusCode(response, HttpStatus.CREATED);
    }

    public static void assert403Forbidden(Response response) {
        assertStatusCode(response, HttpStatus.FORBIDDEN);
    }

    public static void assert404NotFound(Response response) {
        assertStatusCode(response, HttpStatus.NOT_FOUND);
    }

    public static void assert500InternalServerError(Response response) {
        assertStatusCode(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void assertKeyEquals(Response response, String jsonPath, String expected) {
        Assert.assertEquals(response.jsonPath().getString(jsonPath), expected,
                "Expected " + jsonPath + " to be " + expected);
    }

    public static void assertKeyNotNull(Response response, String jsonPath) {
        Assert.assertNotNull(response.jsonPath().getString(jsonPath),
                "Expected " + jsonPath + " to not be null");
    }
}