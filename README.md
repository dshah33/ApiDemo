# Restful Booker API Test Automation Framework

REST API test automation for the [Restful Booker API](https://restful-booker.herokuapp.com), built with Rest Assured, TestNG, Log4J2, and ExtentReports.

--

## Prerequisites

| Tool  | Version | Notes                     |
|-------|---------|---------------------------|
| Java  | 14+     | `java -version` to verify |
| Maven | 3.6+    | `mvn -version` to verify  |

No browser or driver downloads required - this is a pure API testing framework.

--

## Project Structure

```
APIDemo/
├── pom.xml                                        # Maven config & dependencies
├── configuration/
│   └── application.properties                     # Base URL & API endpoint mappings
├── src/main/java/com/api/
│   ├── base/TestBase.java                         # Base test class (setup, logging, reporting)
│   ├── report/ExtentManager.java                  # ExtentReports singleton
│   └── pojo/                                      # Request POJOs
│       ├── Login.java                             # Auth credentials
│       ├── Booking.java                           # Booking payload
│       └── BookingDates.java                      # Check-in/check-out dates
├── src/main/resources/
│   └── log4j2.properties                          # Console + rolling file log config
├── src/test/java/com/api/
│   ├── steps/PrerequiredSteps.java                # Precondition helpers (auth, booking creation)
│   └── tests/                                     # Test classes
│       ├── LoginTest.java                         # POST /auth tests
│       ├── CreateBookingTest.java                 # POST /booking tests
│       ├── GetBookingTest.java                    # GET /booking tests
│       └── UpdateDeleteBookingTest.java           # PUT & DELETE /booking tests
└── src/test/resources/
    ├── schemas/                                   # JSON Schema files for response validation
    └── suites/                                    # TestNG suite XMLs
        ├── main.xml                               # Full regression (login + booking)
        ├── login.xml                              # Auth tests only
        └── booking.xml                            # Booking CRUD tests only
```

--

## Quick Start

```bash
# Clone and enter the project
git clone <repo-url> && cd APIDemo

# Run the full suite (default: main.xml)
mvn clean test

# Run a specific suite
mvn clean test -Dsuite.name=login.xml
mvn clean test -Dsuite.name=booking.xml
```

--

## Configuration

Base URL and API endpoints are defined in `configuration/application.properties`:

```properties
base.uri=https://restful-booker.herokuapp.com
base.path=

api.post.auth=/auth
api.get.bookings=/booking
api.post.booking=/booking
api.get.booking=/booking/{id}
api.put.booking=/booking/{id}
api.patch.booking=/booking/{id}
api.delete.booking=/booking/{id}
```

To switch environments, update `base.uri` in the properties file. No code changes needed.

--

## Test Coverage

### Authentication Tests (`LoginTest.java` - 2 tests)

| Test                                            | Method     | Validates                                                        |
|-------------------------------------------------|------------|------------------------------------------------------------------|
| `POST_Auth_WithValidCredentials_ReturnsToken`   | POST /auth | Valid credentials return 200 OK with a non-null token            |
| `POST_Auth_WithInvalidCredentials_ReturnsError` | POST /auth | Invalid credentials return 200 OK with "Bad credentials" message |

### Create Booking Tests (`CreateBookingTest.java` - 2 tests)

| Test                                   | Method        | Validates                                                                  |
|----------------------------------------|---------------|----------------------------------------------------------------------------|
| `POST_CreateBooking_WithValidData`     | POST /booking | Valid payload returns 200, matches JSON schema, correct firstname/lastname |
| `POST_CreateBooking_NoBody_Returns500` | POST /booking | Missing body returns 500 Internal Server Error                             |

### Get Booking Tests (`GetBookingTest.java` - 3 tests)

| Test                                   | Method                 | Validates                                               |
|----------------------------------------|------------------------|---------------------------------------------------------|
| `GET_AllBookings_Success`              | GET /booking           | Returns 200, booking list matches JSON schema           |
| `GET_Booking_ByValidId_Success`        | GET /booking/:id       | Returns 200, matches schema, correct firstname/lastname |
| `GET_Booking_NonExistentId_Returns404` | GET /booking/999999999 | Returns 404 Not Found                                   |

### Update & Delete Tests (`UpdateDeleteBookingTest.java` - 4 tests)

| Test                                        | Method              | Validates                                                     |
|---------------------------------------------|---------------------|---------------------------------------------------------------|
| `PUT_UpdateBooking_WithValidToken_Success`  | PUT /booking/:id    | Token cookie grants access, returns 200, updated fields match |
| `PUT_UpdateBooking_WithoutToken_Returns403` | PUT /booking/:id    | Missing token returns 403 Forbidden                           |
| `DELETE_Booking_WithValidToken_Success`     | DELETE /booking/:id | Token cookie grants access, returns 201 Created               |
| `DELETE_Booking_WithoutToken_Returns403`    | DELETE /booking/:id | Missing token returns 403 Forbidden                           |

**Total: 11 tests covering full CRUD + auth happy and unhappy paths.**

--

## Reports

After a run, HTML reports are generated in:

```
test-output/extent-reports/BookerAPI_Report_<timestamp>.html
```

Open the HTML file in a browser. Each test includes request details (method, URL, headers, body) and response details
(status, headers, body) logged automatically via a Rest Assured filter.

Log4J logs are also written to:

```
logs/APILogs.log
```

--

## Key Framework Features

- **Standard Rest Assured**: Tests call Rest Assured directly with `given().body().post()` - no abstraction layers
- **Log4J2 Logging**: Console + rolling file logging configured via `log4j2.properties`
- **ExtentReports**: HTML report with automatic request/response logging via a custom Rest Assured filter
- **JSON Schema Validation**: Responses validated against schema files in `src/test/resources/schemas/`
- **POJO Serialization**: Request bodies built using Jackson-annotated POJOs (`Login`, `Booking`, `BookingDates`)
- **Precondition Steps**: `PrerequiredSteps` class provides reusable setup methods (auth token, booking creation)
- **Properties-Driven**: Base URL and all endpoints configured in a single properties file
- **Suite Composition**: `main.xml` includes child suites; individual suites can be targeted via `-Dsuite.name=`

