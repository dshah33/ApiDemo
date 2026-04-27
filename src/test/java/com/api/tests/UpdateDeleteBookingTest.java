package com.api.tests;

import com.api.base.TestBase;
import com.api.pojo.Booking;
import com.api.pojo.BookingDates;
import com.api.steps.PrerequiredSteps;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.api.utils.Assertions.*;

public class UpdateDeleteBookingTest extends TestBase {

    private static String bookerToken;
    private static int commonId;
    private int deleteId;

    @BeforeClass
    @Override
    protected void setUp() {
        super.setUp();
        bookerToken = PrerequiredSteps.getAuthToken(properties, "admin", "password123");
        commonId = PrerequiredSteps.createBooking(properties,
                "Bob", "Update", 300, true,
                "2025-08-01", "2025-08-10", "Gym access");
    }

    @BeforeMethod(onlyForGroups = "delete")
    public void createBookingForDelete() {
        deleteId = PrerequiredSteps.createBooking(properties,
                "Del", "Target", 50, false,
                "2025-09-01", "2025-09-03", "None");
    }

    @Test(priority = 1, description = "PUT /booking/:id - full update with token cookie returns 200")
    public void PUT_UpdateBooking_WithValidToken_Success() {
        Booking updated = new Booking("Bobby", "Updated", 350, false,
                new BookingDates("2025-08-05", "2025-08-12"), "Spa");

        String endpoint = properties.getProperty("api.put.booking").replace("{id}", String.valueOf(commonId));

        Response response = requestWithAuth(bookerToken)
                .contentType("application/json")
                .accept("application/json")
                .body(updated)
                .put(endpoint);

        assert200OK(response);
        assertKeyEquals(response, "firstname", "Bobby");
        assertKeyEquals(response, "lastname", "Updated");
    }

    @Test(priority = 2, description = "PUT /booking/:id - unhappy path: no token returns 403")
    public void PUT_UpdateBooking_WithoutToken_Returns403() {
        Booking body = new Booking("NoAuth", "User", 100, true,
                new BookingDates("2025-08-01", "2025-08-02"), "");

        String endpoint = properties.getProperty("api.put.booking").replace("{id}", String.valueOf(commonId));

        Response response = request()
                .contentType("application/json")
                .accept("application/json")
                .body(body)
                .put(endpoint);

        assert403Forbidden(response);
    }

    @Test(priority = 3, description = "DELETE /booking/:id - valid token returns 201", groups = "delete")
    public void DELETE_Booking_WithValidToken_Success() {
        String endpoint = properties.getProperty("api.delete.booking").replace("{id}", String.valueOf(deleteId));

        Response response = requestWithAuth(bookerToken)
                .contentType("application/json")
                .delete(endpoint);

        assert201Created(response);
    }

    @Test(priority = 4, description = "DELETE /booking/:id - unhappy path: no token returns 403")
    public void DELETE_Booking_WithoutToken_Returns403() {
        String endpoint = properties.getProperty("api.delete.booking").replace("{id}", String.valueOf(commonId));

        Response response = request()
                .contentType("application/json")
                .delete(endpoint);

        assert403Forbidden(response);
    }
}