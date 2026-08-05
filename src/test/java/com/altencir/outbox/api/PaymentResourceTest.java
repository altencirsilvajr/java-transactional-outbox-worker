package com.altencir.outbox.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PaymentResourceTest {

    @Test
    void authorizingPaymentExposesPaymentAndOutboxInOneSnapshot() {
        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "checkout-001")
                .body("""
                        {"amount":42.00,"currency":"brl"}
                        """)
                .when().post("/api/payments")
                .then()
                .statusCode(201)
                .body("currency", equalTo("BRL"))
                .body("outboxStatus", equalTo("PENDING"));

        given()
                .when().get("/api/operations/snapshot")
                .then()
                .statusCode(200)
                .body("payments.findAll { it.idempotencyKey == 'checkout-001' }", hasSize(1))
                .body("messages.findAll { it.idempotencyKey == 'payment-authorized:checkout-001' }", hasSize(1))
                .body("messages.find { it.idempotencyKey == 'payment-authorized:checkout-001' }.eventType", equalTo("PaymentAuthorized.v1"));
    }

    @Test
    void repeatedIdempotencyKeyReturnsOriginalPaymentWithoutNewOutbox() {
        var request = given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "checkout-repeat")
                .body("{\"amount\":19.90,\"currency\":\"BRL\"}");

        request.when().post("/api/payments").then().statusCode(201);
        request.when().post("/api/payments").then().statusCode(200);

        given().when().get("/api/operations/snapshot").then()
                .body("payments.findAll { it.idempotencyKey == 'checkout-repeat' }", hasSize(1))
                .body("messages.findAll { it.idempotencyKey == 'payment-authorized:checkout-repeat' }", hasSize(1));
    }

    @Test
    void invalidPaymentUsesProblemDetails() {
        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "invalid-001")
                .body("{\"amount\":0,\"currency\":\"REAL\"}")
                .when().post("/api/payments")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", equalTo("Invalid payment"));
    }

    @Test
    void reusedKeyWithDifferentPayloadIsRejected() {
        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "checkout-conflict")
                .body("{\"amount\":10.00,\"currency\":\"BRL\"}")
                .when().post("/api/payments")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "checkout-conflict")
                .body("{\"amount\":11.00,\"currency\":\"BRL\"}")
                .when().post("/api/payments")
                .then()
                .statusCode(409)
                .contentType("application/problem+json")
                .body("title", equalTo("Idempotency conflict"));
    }
}
