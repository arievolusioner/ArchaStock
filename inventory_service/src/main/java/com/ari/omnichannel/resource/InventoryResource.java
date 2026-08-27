package com.ari.omnichannel.resource;

import com.ari.omnichannel.dto.InventoryResponse;
import com.ari.omnichannel.dto.StockMovementRequest;
import com.ari.omnichannel.service.InventoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Inventory Operations", description = "Endpoint untuk pergerakan dan pengecekan stok")
public class InventoryResource {

    @Inject
    InventoryService inventoryService;

    @POST
    @Path("/movement")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Catat mutasi stok (Barang IN / OUT)")
    public Response recordStockMovement(@Valid StockMovementRequest request) {
        InventoryResponse response = inventoryService.recordStockMovement(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{warehouseId}/{variantId}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Cek ketersediaan stok spesifik")
    public Response getInventory(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("variantId") UUID variantId) {
        InventoryResponse response = inventoryService.getInventory(warehouseId, variantId);
        return Response.ok(response).build();
    }
}