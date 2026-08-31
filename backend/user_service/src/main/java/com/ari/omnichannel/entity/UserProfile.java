package com.ari.omnichannel.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    public User user;

    @Column(name = "full_name", length = 100, nullable = false)
    public String fullName;

    @Column(name = "phone_number", length = 15)
    public String phoneNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public ZonedDateTime updatedAt;
}
