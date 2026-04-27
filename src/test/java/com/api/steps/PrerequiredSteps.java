package com.api.steps;

import com.api.pojo.Booking;
import com.api.pojo.BookingDates;
import com.api.pojo.Login;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class PrerequiredSteps {

    private static final Logger log = LogManager.getLogger(PrerequiredSteps.class);

    public static String getAuthToken(Properties properties, String username, String password) {
        Login login = new Login(username, password);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(login)
                .post(properties.getProperty("api.post.auth"));

        String token = response.jsonPath().getString("token");
        log.info("Auth token obtained: " + token);
        return token;
    }

    public static int createBooking(Properties properties, String firstname, String lastname,
                                    int totalprice, boolean depositpaid,
                                    String checkin, String checkout, String additionalneeds) {
        Booking booking = new Booking(firstname, lastname, totalprice, depositpaid,
                new BookingDates(checkin, checkout), additionalneeds);

        Response response = RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .body(booking)
                .post(properties.getProperty("api.post.booking"));

        int bookingId = response.jsonPath().getInt("bookingid");
        log.info("Booking created - ID: " + bookingId);
        return bookingId;
    }
}