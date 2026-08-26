package com.ari.omnichannel.product.resource;

import com.ari.omnichannel.common.dto.PageResponse;
import com.ari.omnichannel.product.dto.CreateProductRequest;
import com.ari.omnichannel.product.dto.ProductResponse;
import com.ari.omnichannel.product.dto.UpdateProductRequest;
import com.ari.omnichannel.product.dto.UpdateVariantRequest;
import com.ari.omnichannel.product.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product Management", description = "Endpoints untuk kelola katalog produk")
public class ProductResource {

    @Inject
    ProductService productService;

    @POST
    @Operation(summary = "Tambah produk baru")
    public Response createProduct(@Valid CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Get list produk dengan pagination & pencarian")
    public Response getAllProducts(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("categoryId") UUID categoryId) {

        if (page < 0 ) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;
        PageResponse<ProductResponse> response = productService.getAllProducts(page, size, search, categoryId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get detail produk berdasarkan ID")
    public Response getProductById(@PathParam("id") UUID id) {
        ProductResponse response = productService.getProductById(id);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update data produk")
    public Response updateProduct(@PathParam("id") UUID id, @Valid UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{id}/status")
    @Operation(summary = "Update status aktif/inaktif produk")
    public Response updateStatus(@PathParam("id") UUID id, @QueryParam("isActive") Boolean isActive) {
        if (isActive == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Parameter isActive tidak boleh kosong (true/false)")
                    .build();
        }

        ProductResponse response = productService.updateStatus(id, isActive);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Soft delete produk (set status ke ARCHIVED)")
    public Response deleteProduct(@PathParam("id") UUID id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }

    // Variant
    @PUT
    @Path("/variants/{variantId}")
    @Operation(summary = "Update data spesifik sebuah varian (Harga, SKU, dll)")
    public Response updateVariant(@PathParam("variantId") UUID variantId, @Valid UpdateVariantRequest request) {
        ProductResponse.VariantDto response = productService.updateVariant(variantId, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/variants/{variantId}")
    @Operation(summary = "Soft delete varian tertentu (set isActive ke false)")
    public Response deleteVariant(@PathParam("variantId") UUID variantId) {
        productService.deleteVariant(variantId);
        return Response.noContent().build();
    }
}