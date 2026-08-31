package com.ari.omnichannel.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public record UpdateVariantRequest(
        @NotBlank(message = "SKU tidak boleh kosong")
        String sku,

        @NotNull(message = "Harga tidak boleh kosong")
        @Min(value = 0, message = "Harga tidak boleh negatif")
        BigDecimal price,

        @Min(value = 0, message = "Berat tidak boleh negatif")
        Integer weightGrams,

        Map<String, Object> attributes
) {}