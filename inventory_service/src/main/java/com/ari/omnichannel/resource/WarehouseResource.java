package com.ari.omnichannel.resource;

import com.ari.omnichannel.dto.CreateWarehouseRequest;
import com.ari.omnichannel.dto.WarehouseResponse;
import com.ari.omnichannel.service.InventoryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Warehouse Management", description = "Endpoint untuk kelola master data gudang")
public class WarehouseResource {

    @Inject
    InventoryService inventoryService;

    @POST
    @Operation(summary = "Daftarkan gudang baru")
    public Response createWarehouse(@Valid CreateWarehouseRequest request) {
        WarehouseResponse response = inventoryService.createWarehouse(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}