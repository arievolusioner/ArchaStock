package com.ari.omnichannel.resource;

import com.ari.omnichannel.dto.ChangePasswordRequest;
import com.ari.omnichannel.dto.RegisterUserRequest;
import com.ari.omnichannel.dto.UpdateUserRequest;
import com.ari.omnichannel.dto.UserResponse;
import com.ari.omnichannel.service.UserService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @POST
    @RolesAllowed("ADMIN")
    public Uni<Response> register(@Valid RegisterUserRequest request) {
        return userService.registerUser(request)
                .map(userResponse -> Response
                        .status(Response.Status.CREATED)
                        .entity(userResponse)
                        .build());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"OWNER", "ADMIN"})
    public Uni<UserResponse> getUserById(@PathParam("id") UUID id) {
        return userService.getUserById(id);
    }

    // Get all user
    @GET
    @RolesAllowed({"OWNER", "ADMIN"})
    public Uni<List<UserResponse>> getAllUsers() {
        return userService.getAllUsers();
    }

    // Update user - profiles
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Uni<UserResponse> updateUser(@PathParam("id") UUID id, @Valid UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    // Update user - password
    @PATCH
    @Path("/change-password")
    @Authenticated
    public Uni<Response> changePassword(@Valid ChangePasswordRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return userService.changePassword(userId, request)
                .map(v -> Response.ok(Map.of("message", "Password berhasil diperbarui.")).build());
    }

    // Soft delete / Toggle status aktif user
    @PATCH
    @Path("/{id}/status")
    @RolesAllowed("ADMIN")
    public Uni<UserResponse> toggleUserStatus(@PathParam("id") UUID id, @QueryParam("active") boolean active) {
        return userService.toggleUserStatus(id, active);
    }

}