package com.altencir.outbox.api;

import com.altencir.outbox.application.OperationsQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/operations")
@Produces(MediaType.APPLICATION_JSON)
public class OperationsResource {
    @Inject
    OperationsQuery operationsQuery;

    @GET
    @Path("/snapshot")
    public Object snapshot() {
        return operationsQuery.snapshot();
    }
}
