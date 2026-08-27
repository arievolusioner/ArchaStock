package com.ari.omnichannel.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWarehouseRequest(

        @NotBlank(message = "Nama gudang tidak boleh kosong")
        String name,
        String address
) {}