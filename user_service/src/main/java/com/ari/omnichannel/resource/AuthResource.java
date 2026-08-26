package com.ari.omnichannel.resource;

import com.ari.omnichannel.dto.LoginRequest;
import com.ari.omnichannel.dto.LoginResponse;
import com.ari.omnichannel.dto.UserResponse;
import com.ari.omnichannel.service.AuthService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Uni<LoginResponse> login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @GET
    @Path("/me")
    @Authenticated
    public Uni<UserResponse> getCurrentUser() {
        UUID userId = UUID.fromString(jwt.getSubject());
        return authService.getProfile(userId);
    }
}
