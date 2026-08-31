package com.ari.omnichannel.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public record CreateProductRequest(

        @NotNull(message = "Category ID tidak boleh kosong")
        UUID categoryId,

        @NotBlank(message = "Nama produk tidak boleh kosong")
        String name,
        String description,
        String brand,

        // Nested JSON
        @NotEmpty(message = "Produk minimal mempunyai 1 varian")
        @Valid
        List<VariantRequest> variants
) {
      public record VariantRequest(
              @NotBlank(message = "SKU tidak boleh kosong")
              String sku,

              @NotNull(message = "Harga tidak boleh kosong")
              @Min(value = 0, message = "Harga tidak boleh negatif")
              BigDecimal price,

              @Min(value = 0, message = "Berat tidak boleh negatif")
              Integer weightGrams,

              Map<String, Object> attributes

      )  {}
}
