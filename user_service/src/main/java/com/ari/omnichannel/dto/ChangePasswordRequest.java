package com.ari.omnichannel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Password lama wajib diisi")
    public String oldPassword;

    @NotBlank(message = "Password baru wajib diisi")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    public String newPassword;
}
