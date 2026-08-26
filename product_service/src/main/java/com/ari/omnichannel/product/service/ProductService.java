package com.ari.omnichannel.product.service;

import com.ari.omnichannel.common.dto.PageResponse;
import com.ari.omnichannel.product.dto.CreateProductRequest;
import com.ari.omnichannel.product.dto.UpdateProductRequest;
import com.ari.omnichannel.product.dto.UpdateVariantRequest;
import com.ari.omnichannel.product.dto.ProductResponse;
import com.ari.omnichannel.product.entity.Category;
import com.ari.omnichannel.product.entity.Product;
import com.ari.omnichannel.product.entity.ProductVariant;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        // 1. Validasi SKU
        for (CreateProductRequest.VariantRequest varReq : request.variants()) {
            if (ProductVariant.find("sku", varReq.sku()).firstResultOptional().isPresent()) {
                throw new WebApplicationException("Varian dengan SKU " + varReq.sku() + " sudah ada", 400);
            }
        }

        // 2. Validasi Kategori
        Category category = Category.<Category>findByIdOptional(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Kategori dengan ID tersebut tidak ditemukan"));

        // 3. Simpan Master Product
        Product product = new Product();
        product.setCategory(category);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setIsActive(true);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());
        product.persist();

        // 4. Looping untuk Simpan Multi-Varian Product
        List<ProductVariant> savedVariants = new ArrayList<>();
        for (CreateProductRequest.VariantRequest varReq : request.variants()) {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);
            variant.setSku(varReq.sku());
            variant.setPrice(varReq.price());
            variant.setCostPrice(BigDecimal.ZERO);

            // Handle Attributes JSONB
            if (varReq.attributes() != null) {
                variant.setAttributes(varReq.attributes());
            } else {
                variant.setAttributes(new java.util.HashMap<>());
            }

            // Handle optional weight
            variant.setWeightGrams(varReq.weightGrams() != null ? varReq.weightGrams() : 0);

            variant.setIsActive(true);
            variant.setCreatedAt(OffsetDateTime.now());
            variant.setUpdatedAt(OffsetDateTime.now());

            variant.persist();
            savedVariants.add(variant);
        }

        // Link variant ke product
        product.setVariants(savedVariants);

        return mapToResponse(product);
    }

    public PageResponse<ProductResponse> getAllProducts(int page, int size, String search, UUID categoryId) {
        PanacheQuery<Product> query;

        StringBuilder queryStr = new StringBuilder("1=1");
        java.util.Map<String, Object> params = new java.util.HashMap<>();

        if (search != null && !search.isBlank()) {
            queryStr.append(" AND LOWER(name) LIKE :search");
            params.put("search ", "%" + search.toLowerCase() + "%");
        }

        if (categoryId != null ) {
            queryStr.append(" AND category.id = :categoryId");
            params.put("categoryId", categoryId);
        }

        query = Product.findAll(Sort.by("createdAt").descending());

        PanacheQuery<Product> pagedQuery = query.page(Page.of(page, size));
        List<ProductResponse> content = pagedQuery.list().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content, page, size, query.count(), pagedQuery.pageCount()
        );
    }

    public ProductResponse getProductById(UUID id) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product dengan ID " + id + " tidak ditemukan"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setUpdatedAt(OffsetDateTime.now());

        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateStatus(UUID id, Boolean isActive) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product dengan ID " + id + " tidak ditemukan"));

        product.setIsActive(isActive);
        product.setUpdatedAt(OffsetDateTime.now());

        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Product dengan ID " + id + " tidak ditemukan"));

        // Soft delete
        product.setIsActive(false);
        product.setUpdatedAt(OffsetDateTime.now());
    }

    private ProductResponse mapToResponse(Product product) {
        List<ProductResponse.VariantDto> variantDtos = product.getVariants() == null ? List.of() :
                product.getVariants().stream()
                        .map(v -> new ProductResponse.VariantDto(v.getId(), v.getSku(), v.getPrice(), v.getIsActive()))
                        .collect(Collectors.toList());

        return new ProductResponse(
                product.getId(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getIsActive(),
                variantDtos,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Transactional
    public ProductResponse.VariantDto updateVariant(UUID variantId, UpdateVariantRequest request) {
        ProductVariant variant = ProductVariant.<ProductVariant>findByIdOptional(variantId)
                .orElseThrow(() -> new NotFoundException("Varian dengan ID " + variantId + " tidak ditemukan"));

        // Validasi: Pastikan SKU baru tidak dipakai oleh varian lain
        if (!variant.getSku().equals(request.sku())) {
            if (ProductVariant.find("sku", request.sku()).firstResultOptional().isPresent()) {
                throw new WebApplicationException("Varian dengan SKU " + request.sku() + " sudah digunakan", 400);
            }
        }

        variant.setSku(request.sku());
        variant.setPrice(request.price());
        variant.setWeightGrams(request.weightGrams() != null ? request.weightGrams() : 0);

        // Update attributes jika dikirim dari frontend
        if (request.attributes() != null) {
            variant.setAttributes(request.attributes());
        }

        variant.setUpdatedAt(OffsetDateTime.now());

        return new ProductResponse.VariantDto(
                variant.getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getIsActive()
        );
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        ProductVariant variant = ProductVariant.<ProductVariant>findByIdOptional(variantId)
                .orElseThrow(() -> new NotFoundException("Varian dengan ID " + variantId + " tidak ditemukan"));

        // Soft delete agar riwayat pesanan (order) tidak error di kemudian hari
        variant.setIsActive(false);
        variant.setUpdatedAt(OffsetDateTime.now());
    }
}