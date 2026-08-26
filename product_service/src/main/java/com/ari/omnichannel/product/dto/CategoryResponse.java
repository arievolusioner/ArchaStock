package com.ari.omnichannel.product.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}