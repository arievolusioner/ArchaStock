package com.ari.omnichannel.service;

import com.ari.omnichannel.dto.ChangePasswordRequest;
import com.ari.omnichannel.dto.RegisterUserRequest;
import com.ari.omnichannel.dto.UpdateUserRequest;
import com.ari.omnichannel.dto.UserResponse;
import com.ari.omnichannel.entity.User;
import com.ari.omnichannel.entity.UserProfile;
import com.ari.omnichannel.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @WithTransaction
    public Uni<UserResponse> registerUser(RegisterUserRequest request) {
        return userRepository.findByUsername(request.username)
                .onItem().ifNotNull().failWith(() ->
                        new WebApplicationException("Username sudah terdaftar.", Response.Status.CONFLICT)
                )
                .onItem().ifNull().switchTo(() -> {
                    User user = new User();
                    user.username = request.username;
                    user.password = BcryptUtil.bcryptHash(request.password);
                    user.role = request.role;

                    UserProfile profile = new UserProfile();
                    profile.fullName = request.fullName;
                    profile.phoneNumber = request.phoneNumber;
                    profile.user = user;

                    user.profile = profile;

                    return userRepository.persist(user);
                })

                .map(UserResponse::fromEntity);
    }

    @WithSession
    public Uni<UserResponse> getUserById(UUID id) {
        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = ?1", id)
                .firstResult()
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("User tidak ditemukan", Response.Status.NOT_FOUND)
                )
                .map(UserResponse::fromEntity);
    }

    @WithSession
    public Uni<List<UserResponse>> getAllUsers() {
        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile").list()
                .map(users -> users.stream()
                        .map(UserResponse::fromEntity)
                        .toList()
                );
    }

    @WithTransaction
    public Uni<UserResponse> updateUser(UUID id, UpdateUserRequest request) {
        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = ?1", id)
                .firstResult()
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("User tidak ditemukan", Response.Status.NOT_FOUND)
                )
                .map(user -> {
                    user.role = request.role;

                    if (user.profile != null) {
                        user.profile.fullName = request.fullName;
                        user.profile.phoneNumber = request.phoneNumber;
                    }

                    return user;
                })
                .map(UserResponse::fromEntity);
    }

    @WithTransaction
    public Uni<UserResponse> toggleUserStatus(UUID id, boolean isActive) {
        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = ?1", id)
                .firstResult()
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("User tidak ditemukan", Response.Status.NOT_FOUND)
                )
                .map(user -> {
                    user.isActive = isActive;
                    return user;
                })
                .map(UserResponse::fromEntity);
    }

    @WithTransaction
    public Uni<Void> changePassword(UUID userId, ChangePasswordRequest request) {
        return userRepository.findById(userId)
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("User tidak ditemukan.", Response.Status.NOT_FOUND)
                )
                .map(user -> {
                    if (!BcryptUtil.matches(request.oldPassword, user.password)) {
                        throw new WebApplicationException("Password lama salah.", Response.Status.BAD_REQUEST);
                    }
                    user.password = BcryptUtil.bcryptHash(request.newPassword);
                    return user;
                })
                .replaceWithVoid();
    }

}