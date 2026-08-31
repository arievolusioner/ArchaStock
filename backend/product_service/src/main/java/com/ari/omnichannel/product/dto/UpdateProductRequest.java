package com.ari.omnichannel.product.dto;


import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequest(

        @NotBlank(message = "Nama produk tidak boleh kosong")
        String name,
        String description,
        String brand

) {}
