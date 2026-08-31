package com.ari.omnichannel.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "Nama kategori tidak boleh kosong")
        String name,
        String description
) {}