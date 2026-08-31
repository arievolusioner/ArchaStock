package com.ari.omnichannel.dto;

import com.ari.omnichannel.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRequest {

    @NotBlank(message = "Nama lengkap tidak boleh kosong.")
    public String fullName;

    public String phoneNumber;

    @NotNull(message = "Role tidak boleh kosong.")
    public Role role;
}
