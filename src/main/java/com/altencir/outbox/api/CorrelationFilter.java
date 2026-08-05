package com.altencir.outbox.api;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;
import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
    public static final String HEADER = "X-Correlation-ID";
    private static final String PROPERTY = CorrelationFilter.class.getName() + ".correlationId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var supplied = requestContext.getHeaderString(HEADER);
        var correlationId = supplied == null || supplied.isBlank() ? UUID.randomUUID().toString() : supplied;
        requestContext.setProperty(PROPERTY, correlationId);
        MDC.put("correlationId", correlationId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle(HEADER, requestContext.getProperty(PROPERTY));
        MDC.remove("correlationId");
    }
}
