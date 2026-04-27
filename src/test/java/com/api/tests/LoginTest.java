package com.api.tests;

import com.api.base.TestBase;
import com.api.pojo.Login;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.api.utils.Assertions.*;

public class LoginTest extends TestBase {

    @Test(priority = 1, description = "POST /auth - valid credentials returns token")
    public void POST_Auth_WithValidCredentials_ReturnsToken() {
        Login login = new Login("admin", "password123");

        Response response = request()
                .contentType("application/json")
                .body(login)
                .post(properties.getProperty("api.post.auth"));

        assert200OK(response);
        assertKeyNotNull(response, "token");
    }

    @Test(priority = 2, description = "POST /auth - invalid credentials returns error")
    public void POST_Auth_WithInvalidCredentials_ReturnsError() {
        Login login = new Login("wronguser", "wrongpass");

        Response response = request()
                .contentType("application/json")
                .body(login)
                .post(properties.getProperty("api.post.auth"));

        assert200OK(response);
        assertKeyEquals(response, "reason", "Bad credentials");
    }
}