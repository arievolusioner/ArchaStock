package com.ari.omnichannel.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name= "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(length = 50, unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    public Role role;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    public boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public ZonedDateTime updatedAt;

    // Relasi One-to-One ke UserProfile
    @OneToOne(mappedBy = "user",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public UserProfile profile;
}
