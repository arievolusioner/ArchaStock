package com.ari.omnichannel.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String name,
        String address,
        boolean isActive,
        OffsetDateTime createdAt
) {}