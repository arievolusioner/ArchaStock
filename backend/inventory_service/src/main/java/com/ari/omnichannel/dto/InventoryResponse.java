package com.ari.omnichannel.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID inventoryId,
        UUID warehouseId,
        String warehouseName,
        UUID variantId,
        Integer quantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        OffsetDateTime updatedAt
) {}