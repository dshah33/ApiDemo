package com.api.base;

import com.api.report.ExtentManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestBase {

    protected static final Logger log = LogManager.getLogger(TestBase.class);
    protected Properties properties;

    @BeforeClass
    protected void setUp() {
        loadProperties();
        RestAssured.baseURI = properties.getProperty("base.uri");
        RestAssured.basePath = properties.getProperty("base.path", "");
        log.info("Base URI set to: " + RestAssured.baseURI + RestAssured.basePath);
    }

    private void loadProperties() {
        properties = new Properties();
        String path = System.getProperty("user.dir") + "/configuration/application.properties";
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            log.error("Failed to load application.properties", e);
        }
    }

    protected RequestSpecification request() {
        return RestAssured.given().filter(new ExtentReportFilter());
    }

    protected RequestSpecification requestWithAuth(String token) {
        return RestAssured.given().filter(new ExtentReportFilter()).cookie("token", token);
    }

    @BeforeMethod
    protected void startTest(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentManager.createTest(testName, description);
        log.info("========== Starting Test: " + testName + " ==========");
    }

    @AfterMethod
    protected void endTest(ITestResult result) {
        ExtentTest extentTest = ExtentManager.getTest();
        String testName = result.getMethod().getMethodName();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                log.info("Test PASSED: " + testName);
                extentTest.log(Status.PASS, "Test passed");
                break;
            case ITestResult.FAILURE:
                log.error("Test FAILED: " + testName);
                extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
                break;
            case ITestResult.SKIP:
                log.warn("Test SKIPPED: " + testName);
                extentTest.log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
                break;
        }
        log.info("========== Finished Test: " + testName + " ==========");
        ExtentManager.removeTest();
    }

    @AfterSuite
    protected void tearDown() {
        ExtentManager.flush();
        RestAssured.reset();
    }

    private static class ExtentReportFilter implements Filter {

        @Override
        public Response filter(FilterableRequestSpecification requestSpec,
                               FilterableResponseSpecification responseSpec,
                               FilterContext ctx) {

            // --- Log Request ---
            String method = requestSpec.getMethod();
            String url = requestSpec.getURI();
            StringBuilder requestHeaders = new StringBuilder();
            for (Header header : requestSpec.getHeaders()) {
                requestHeaders.append(header.getName()).append(": ").append(header.getValue()).append("\n");
            }
            String requestBody = (requestSpec.getBody() != null) ? requestSpec.getBody().toString() : "";

            log.info("Request Method: " + method);
            log.info("Request URL: " + url);
            log.info("Request Headers:\n" + requestHeaders);
            if (!requestBody.isEmpty()) {
                log.info("Request Body:\n" + requestBody);
            }

            ExtentTest extentTest = ExtentManager.getTest();
            if (extentTest != null) {
                extentTest.info("Request Method: " + method);
                extentTest.info("Request URL: " + url);
                extentTest.info("Request Headers:\n" + requestHeaders);
                if (!requestBody.isEmpty()) {
                    extentTest.info(MarkupHelper.createCodeBlock(requestBody, CodeLanguage.JSON));
                }
            }

            // --- Execute Request ---
            Response response = ctx.next(requestSpec, responseSpec);

            // --- Log Response ---
            int statusCode = response.getStatusCode();
            String statusLine = response.getStatusLine();
            StringBuilder responseHeaders = new StringBuilder();
            for (Header header : response.getHeaders()) {
                responseHeaders.append(header.getName()).append(": ").append(header.getValue()).append("\n");
            }
            String responseBody = response.getBody().asString();

            log.info("Response Status: " + statusCode + " " + statusLine);
            log.info("Response Headers:\n" + responseHeaders);
            log.info("Response Body:\n" + responseBody);

            if (extentTest != null) {
                extentTest.info("Response Status: " + statusLine);
                extentTest.info("Response Headers:\n" + responseHeaders);
                extentTest.info(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
            }

            return response;
        }
    }
}
