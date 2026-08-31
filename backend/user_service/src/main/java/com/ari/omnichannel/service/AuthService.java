package com.ari.omnichannel.service;

import com.ari.omnichannel.dto.LoginRequest;
import com.ari.omnichannel.dto.LoginResponse;
import com.ari.omnichannel.dto.UserResponse;
import com.ari.omnichannel.entity.User;
import com.ari.omnichannel.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @WithSession
    public Uni<LoginResponse> login(LoginRequest request) {

        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.username = ?1", request.username)
                .firstResult()
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("Username atau password salah", Response.Status.UNAUTHORIZED)
                )
                .onItem().transformToUni(user -> {
                    if (!user.isActive) {
                        return Uni.createFrom().failure(
                                new WebApplicationException("Akun Anda telah dinonaktifkan. Silakan hubungi Admin.", Response.Status.FORBIDDEN)
                        );
                    }
                    if (!BcryptUtil.matches(request.password, user.password)) {
                        return Uni.createFrom().failure(
                                new WebApplicationException("Username atau password salah", Response.Status.UNAUTHORIZED)
                        );
                    }

                    long durationSeconds = 28800; // 8 Jam
                    String token = Jwt.issuer("https://omnichannel.ari.com/issuer")
                            .upn(user.username)
                            .subject(user.id.toString())
                            .groups(Set.of(user.role.name()))
                            .expiresIn(Duration.ofSeconds(durationSeconds))
                            .sign();

                    LoginResponse response = new LoginResponse(token, durationSeconds, UserResponse.fromEntity(user));

                    return Uni.createFrom().item(response);
                });
    }

    @WithSession
    public Uni<UserResponse> getProfile(UUID userId) {
        return userRepository.find("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = ?1", userId)
                .firstResult()
                .onItem().ifNull().failWith(() -> new NotFoundException("User tidak ditemukan."))
                .map(UserResponse::fromEntity);
    }
}