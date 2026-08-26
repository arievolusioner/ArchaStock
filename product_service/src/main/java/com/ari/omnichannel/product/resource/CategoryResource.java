package com.ari.omnichannel.product.resource;

import com.ari.omnichannel.product.dto.CategoryResponse;
import com.ari.omnichannel.product.dto.CreateCategoryRequest;
import com.ari.omnichannel.product.service.CategoryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Category Management", description = "Endpoints untuk kelola master kategori")
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @POST
    @Operation(summary = "Tambah kategori baru (Slug generate otomatis)")
    public Response createCategory(@Valid CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Get semua list kategori dengan pagination & search")
    public Response getAllCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search) {

        // Praktik defensif: Cegah input aneh/ekstrem
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        return Response.ok(categoryService.getAllCategories(page, size, search)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get detail kategori berdasarkan ID")
    public Response getCategoryById(@PathParam("id") UUID id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update data kategori")
    public Response updateCategory(@PathParam("id") UUID id, @Valid CreateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Hard delete kategori (Hanya jika belum dipakai produk)")
    public Response deleteCategory(@PathParam("id") UUID id) {
        categoryService.deleteCategory(id);
        return Response.noContent().build();
    }
}