package com.shop.brew;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class BrewResourceTest {
    @Test
    void testBrewEndpoint() {
        given()
          .when().get("/brew")
          .then()
             .statusCode(200)
             .body(is("Hello from Brew Service!"));
    }

}
