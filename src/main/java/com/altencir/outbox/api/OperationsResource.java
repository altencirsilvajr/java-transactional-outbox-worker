package com.altencir.outbox.api;

import com.altencir.outbox.application.OperationsQuery;
import com.altencir.outbox.infrastructure.FailureSwitch;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/operations")
@Produces(MediaType.APPLICATION_JSON)
public class OperationsResource {
    @Inject
    OperationsQuery operationsQuery;

    @Inject
    FailureSwitch failureSwitch;

    @GET
    @Path("/snapshot")
    public Object snapshot() {
        return operationsQuery.snapshot();
    }

    @POST
    @Path("/fail-next-publication")
    public void failNextPublication() {
        failureSwitch.arm();
    }
}
