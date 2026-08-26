package com.ari.omnichannel.product.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String categoryName,
        String name,
        String description,
        String brand,
        Boolean isActive,
        List<VariantDto> variants,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public record VariantDto(
            UUID id,
            String sku,
            java.math.BigDecimal price,
            Boolean isActive
    ) {}
}