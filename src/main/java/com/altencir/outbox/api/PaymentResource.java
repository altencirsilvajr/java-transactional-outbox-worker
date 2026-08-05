package com.altencir.outbox.api;

import com.altencir.outbox.application.PaymentContracts.CreatePaymentRequest;
import com.altencir.outbox.application.PaymentService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/api/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentResource {
    @Inject
    PaymentService paymentService;

    @POST
    public Response authorize(@HeaderParam("Idempotency-Key") String requestKey, @Valid CreatePaymentRequest request) {
        var result = paymentService.authorize(requestKey, request);
        if (result.created()) {
            return Response.created(URI.create("/api/payments/" + result.response().paymentId()))
                    .entity(result.response())
                    .build();
        }
        return Response.ok(result.response()).build();
    }
}
