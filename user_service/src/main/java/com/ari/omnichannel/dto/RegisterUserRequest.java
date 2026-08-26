package com.ari.omnichannel.dto;

import com.ari.omnichannel.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterUserRequest {

    @NotBlank(message = "Username tidak boleh kosong!")
    @Size(min = 4, max = 20, message = "Userame harus antara 4-20 karakter!")
    public String username;

    @NotBlank(message = "Password tidak boleh kosong!")
    @Size(min = 8, message = "Password minimal 8 karakter!")
    public String password;

    @NotNull(message = "Role tidak boleh kosong")
    public Role role;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    public String fullName;

    public String phoneNumber;
}
