package com.ari.omnichannel.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username tidak boleh kosong.")
    public String username;

    @NotBlank(message = "Password tidak boleh kosong.")
    public String password;
}