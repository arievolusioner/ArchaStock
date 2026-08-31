package com.ari.omnichannel.dto;

import com.ari.omnichannel.entity.Role;
import com.ari.omnichannel.entity.User;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserResponse {

    public UUID id;
    public String username;
    public Role role;
    public boolean isActive;
    public String fullName;
    public String phoneNumber;
    public ZonedDateTime createdAt;

    // Method memetakan dari entity ke dto
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.id = user.id;
        response.username = user.username;
        response.role = user.role;
        response.isActive = user.isActive;
        response.createdAt = user.createdAt;

        if (user.profile != null) {
            response.fullName = user.profile.fullName;
            response.phoneNumber = user.profile.phoneNumber;
        }

        return response;
    }
}
