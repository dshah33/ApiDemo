package com.api.tests;

import com.api.base.TestBase;
import com.api.steps.PrerequiredSteps;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.api.utils.Assertions.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class GetBookingTest extends TestBase {

    private static int createdBookingId;
    private static final String FIRSTNAME = "Test";
    private static final String LASTNAME = "User";

    @BeforeClass
    @Override
    protected void setUp() {
        super.setUp();
        createdBookingId = PrerequiredSteps.createBooking(properties,
                FIRSTNAME, LASTNAME, 200, true,
                "2025-05-01", "2025-05-07", "Airport transfer");
    }

    @Test(priority = 1, description = "GET /booking - retrieve all booking IDs")
    public void GET_AllBookings_Success() {
        Response response = request()
                .accept("application/json")
                .get(properties.getProperty("api.get.bookings"));

        assert200OK(response);
        response.then().body(matchesJsonSchemaInClasspath("schemas/booking_list_schema.json"));
    }

    @Test(priority = 2, description = "GET /booking/:id - retrieve a single booking")
    public void GET_Booking_ByValidId_Success() {
        String endpoint = properties.getProperty("api.get.booking").replace("{id}", String.valueOf(createdBookingId));

        Response response = request()
                .accept("application/json")
                .get(endpoint);

        assert200OK(response);
        response.then().body(matchesJsonSchemaInClasspath("schemas/get_booking_schema.json"));
        assertKeyEquals(response, "firstname", FIRSTNAME);
        assertKeyEquals(response, "lastname", LASTNAME);
    }

    @Test(priority = 3, description = "GET /booking/:id - unhappy path: non-existent ID returns 404")
    public void GET_Booking_NonExistentId_Returns404() {
        String endpoint = properties.getProperty("api.get.booking").replace("{id}", "999999999");

        Response response = request()
                .accept("application/json")
                .get(endpoint);

        assert404NotFound(response);
    }
}