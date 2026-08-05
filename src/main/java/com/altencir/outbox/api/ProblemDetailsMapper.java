package com.altencir.outbox.api;

import com.altencir.outbox.application.DomainValidationException;
import com.altencir.outbox.application.IdempotencyConflictException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.net.URI;

public final class ProblemDetailsMapper {
    private ProblemDetailsMapper() {
    }

    public record Problem(String type, String title, int status, String detail) {
    }

    private static Response response(int status, String title, String detail) {
        return Response.status(status)
                .type("application/problem+json")
                .entity(new Problem("about:blank", title, status, detail))
                .location(URI.create("about:blank"))
                .build();
    }

    @Provider
    public static class ValidationMapper implements ExceptionMapper<DomainValidationException> {
        @Override
        public Response toResponse(DomainValidationException exception) {
            return response(400, "Invalid payment", exception.getMessage());
        }
    }

    @Provider
    public static class ConstraintMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException exception) {
            return response(400, "Invalid payment", exception.getMessage());
        }
    }

    @Provider
    public static class ConflictMapper implements ExceptionMapper<IdempotencyConflictException> {
        @Override
        public Response toResponse(IdempotencyConflictException exception) {
            return response(409, "Idempotency conflict", exception.getMessage());
        }
    }
}
