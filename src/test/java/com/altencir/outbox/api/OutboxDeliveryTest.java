package com.altencir.outbox.api;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OutboxDeliveryTest {

    @Test
    void workerPublishesAndConsumerRecordsOneIdempotentEvidence() {
        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "delivery-001")
                .body("{\"amount\":73.25,\"currency\":\"BRL\"}")
                .when().post("/api/payments")
                .then().statusCode(201);

        await().atMost(15, SECONDS).pollInterval(java.time.Duration.ofMillis(250)).untilAsserted(() ->
                given().when().get("/api/operations/snapshot").then()
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-001' }.status", equalTo("PUBLISHED"))
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-001' }.attemptCount", equalTo(1))
                        .body("consumedEvents.findAll { it.idempotencyKey == 'payment-authorized:delivery-001' }", hasSize(1)));
    }

    @Test
    void transientKafkaFailureIsObservableAndEventuallyRecovers() {
        given().when().post("/api/operations/fail-next-publication").then().statusCode(204);
        given()
                .contentType(ContentType.JSON)
                .header("Idempotency-Key", "delivery-retry")
                .body("{\"amount\":91.10,\"currency\":\"BRL\"}")
                .when().post("/api/payments")
                .then().statusCode(201);

        await().atMost(5, SECONDS).pollInterval(java.time.Duration.ofMillis(100)).untilAsserted(() ->
                given().when().get("/api/operations/snapshot").then()
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-retry' }.status", equalTo("PENDING"))
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-retry' }.attemptCount", equalTo(1))
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-retry' }.lastError", equalTo("Controlled Kafka publication failure.")));

        await().atMost(8, SECONDS).pollInterval(java.time.Duration.ofMillis(250)).untilAsserted(() ->
                given().when().get("/api/operations/snapshot").then()
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-retry' }.status", equalTo("PUBLISHED"))
                        .body("messages.find { it.idempotencyKey == 'payment-authorized:delivery-retry' }.attemptCount", equalTo(2))
                        .body("consumedEvents.findAll { it.idempotencyKey == 'payment-authorized:delivery-retry' }", hasSize(1)));
    }
}
