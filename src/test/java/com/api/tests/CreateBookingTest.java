package com.api.tests;

import com.api.base.TestBase;
import com.api.pojo.Booking;
import com.api.pojo.BookingDates;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.api.utils.Assertions.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class CreateBookingTest extends TestBase {

    @Test(priority = 1, description = "POST /booking - create a booking with all valid fields")
    public void POST_CreateBooking_WithValidData() {
        Booking booking = new Booking(
                "Test",
                "User",
                150,
                true,
                new BookingDates("2025-03-01", "2025-03-05"),
                "Breakfast");

        Response response = request().log().all()
                .contentType("application/json")
                .accept("application/json")
                .body(booking)
                .post(properties.getProperty("api.post.booking"));

        assert200OK(response);
        response.then().body(matchesJsonSchemaInClasspath("schemas/create_booking_schema.json"));
        assertKeyEquals(response, "booking.firstname", "Test");
        assertKeyEquals(response, "booking.lastname", "User");
    }

    @Test(priority = 2, description = "POST /booking - unhappy path: no body returns 500")
    public void POST_CreateBooking_NoBody_Returns500() {
        Response response = request()
                .contentType("application/json")
                .accept("application/json")
                .post(properties.getProperty("api.post.booking"));

        assert500InternalServerError(response);
    }
}