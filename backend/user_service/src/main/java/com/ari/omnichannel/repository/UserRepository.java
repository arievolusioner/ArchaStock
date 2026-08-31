package com.ari.omnichannel.repository;

import com.ari.omnichannel.entity.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {

    // Method search user berdasarkan username
    public Uni<User> findByUsername (String username) {
        return find("username", username).firstResult();
    }
}
