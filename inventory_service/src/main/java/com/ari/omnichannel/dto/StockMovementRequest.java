package com.ari.omnichannel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record StockMovementRequest(

        @NotNull(message = "ID Gudang wajib diisi")
        UUID warehouseId,

        @NotNull(message = "ID Gudang wajib diisi")
        UUID variantId,

        @NotNull(message = "Tipe muttasi wajib diisi (IN/OUT)")
        @Pattern(regexp = "^(IN|OUT)$", message = "Tipe pergerakan hanya boleh IN atau OUT")
        String movementType,

        @NotNull(message = "Jumlah mutasi wajib diisi")
        @Min(value = 1, message = "Jumlah pergerakan minimal 1")
        Integer quantity,
        String referenceNumber,
        String notes

) {}
