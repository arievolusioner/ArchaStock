package com.ari.omnichannel.product.service;

import com.ari.omnichannel.common.dto.PageResponse;
import com.ari.omnichannel.product.dto.CategoryResponse;
import com.ari.omnichannel.product.dto.CreateCategoryRequest;
import com.ari.omnichannel.product.entity.Category;
import com.ari.omnichannel.product.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CategoryService {

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        // 1. Generate Slug Otomatis
        String generatedSlug = generateSlug(request.name());

        // 2. Validasi Slug Unik
        if (Category.find("slug", generatedSlug).firstResultOptional().isPresent()) {
            throw new WebApplicationException("Kategori dengan nama tersebut sudah ada", 400);
        }

        // 3. Simpan Kategori
        Category category = new Category();
        category.setName(request.name());
        category.setSlug(generatedSlug);
        category.setDescription(request.description());
        category.setCreatedAt(OffsetDateTime.now());
        category.setUpdatedAt(OffsetDateTime.now());
        category.persist();

        return mapToResponse(category);
    }

    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String search) {
        PanacheQuery<Category> query;

        // Logika pencarian yang fleksibel (case-insensitive)
        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            query = Category.find("LOWER(name) LIKE ?1", Sort.by("createdAt").descending(), searchPattern);
        } else {
            query = Category.findAll(Sort.by("createdAt").descending());
        }

        // Terapkan batas halaman (Pagination)
        PanacheQuery<Category> pagedQuery = query.page(Page.of(page, size));
        List<CategoryResponse> content = pagedQuery.list().stream()
                .map(this::mapToResponse)
                .toList();

        // Kembalikan dalam bentuk objek standar PageResponse
        return new PageResponse<>(
                content, page, size, query.count(), pagedQuery.pageCount()
        );
    }

    public CategoryResponse getCategoryById(UUID id) {
        Category category = Category.<Category>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        Category category = Category.<Category>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));

        String newSlug = generateSlug(request.name());

        // Validasi jika admin mengubah nama yang berujung pada bentrok slug
        if (!category.getSlug().equals(newSlug)) {
            if (Category.find("slug", newSlug).firstResultOptional().isPresent()) {
                throw new WebApplicationException("Nama kategori sudah digunakan", 400);
            }
        }

        category.setName(request.name());
        category.setSlug(newSlug);
        category.setDescription(request.description());
        category.setUpdatedAt(OffsetDateTime.now());

        return mapToResponse(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = Category.<Category>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));

        // Validasi: Cek apakah kategori ini masih dipakai oleh produk
        long productCount = Product.count("category.id", id);
        if (productCount > 0) {
            throw new WebApplicationException("Gagal menghapus! Kategori ini masih digunakan oleh " + productCount + " produk.", 400);
        }

        // Hard delete diizinkan karena sudah aman dari constraint
        category.delete();
    }

    // Utility Method untuk membuat Slug URL-friendly
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "") // Hapus simbol aneh (hanya sisakan huruf, angka, spasi)
                .replaceAll("\\s+", "-");       // Ganti spasi menjadi strip
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}