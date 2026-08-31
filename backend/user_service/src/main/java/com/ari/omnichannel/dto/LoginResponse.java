package com.ari.omnichannel.dto;

public class LoginResponse {

    public String token;
    public String type = "Bearer";
    public Long expiresIn; // Dalam detik
    public UserResponse user;

    public LoginResponse(String token, Long expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
