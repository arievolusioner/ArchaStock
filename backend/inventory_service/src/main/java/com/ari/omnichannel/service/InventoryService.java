package com.ari.omnichannel.service;

import com.ari.omnichannel.dto.*;
import com.ari.omnichannel.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InventoryService {

    // 1. Buat Gudang Baru
    @Transactional
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.name());
        warehouse.setAddress(request.address());
        warehouse.setCreatedAt(OffsetDateTime.now());
        warehouse.persist();

        return mapToWarehouseResponse(warehouse);
    }

    // 2. Logika Mutasi Stok (IN / OUT)
    @Transactional
    public InventoryResponse recordStockMovement(StockMovementRequest request) {
        // Validasi Gudang
        Warehouse warehouse = Warehouse.<Warehouse>findByIdOptional(request.warehouseId())
                .orElseThrow(() -> new NotFoundException("Gudang tidak ditemukan"));

        // Cari data stok saat ini (berdasarkan gudang & varian)
        Optional<Inventory> inventoryOpt = Inventory.find("warehouse.id = ?1 and variantId = ?2",
                warehouse.getId(), request.variantId()).firstResultOptional();

        Inventory inventory;

        if (request.movementType().equals("IN")) {
            // Jika barang masuk dan data stok belum ada, buat baru
            inventory = inventoryOpt.orElseGet(() -> {
                Inventory newInv = new Inventory();
                newInv.setWarehouse(warehouse);
                newInv.setVariantId(request.variantId());
                newInv.setQuantity(0);
                newInv.setReservedQuantity(0);
                return newInv;
            });
            // Tambah stok
            inventory.setQuantity(inventory.getQuantity() + request.quantity());

        } else { // Jika "OUT"
            // Tolak kalau barang mau keluar tapi datanya tidak ada
            inventory = inventoryOpt.orElseThrow(() ->
                    new WebApplicationException("Stok tidak ditemukan untuk varian ini di gudang terkait", 400));

            int availableStock = inventory.getQuantity() - inventory.getReservedQuantity();
            if (availableStock < request.quantity()) {
                throw new WebApplicationException("Stok tidak mencukupi! Sisa stok tersedia: " + availableStock, 400);
            }

            // Kurangi stok
            inventory.setQuantity(inventory.getQuantity() - request.quantity());
        }

        inventory.setUpdatedAt(OffsetDateTime.now());
        inventory.persist();

        // 3. Catat di Buku Besar (Ledger) sebagai Audit Trail
        InventoryLedger ledger = new InventoryLedger();
        ledger.setInventory(inventory);
        ledger.setMovementType(request.movementType());
        ledger.setQuantity(request.quantity());
        ledger.setReferenceNumber(request.referenceNumber());
        ledger.setNotes(request.notes());
        ledger.setCreatedAt(OffsetDateTime.now());
        ledger.persist();

        return mapToInventoryResponse(inventory);
    }

    // Utility Method: Cek Stok Spesifik
    public InventoryResponse getInventory(UUID warehouseId, UUID variantId) {
        Inventory inventory = Inventory.<Inventory>find("warehouse.id = ?1 and variantId = ?2", warehouseId, variantId)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException("Data stok tidak ditemukan"));

        return mapToInventoryResponse(inventory);
    }

    // --- Mapper ---
    private WarehouseResponse mapToWarehouseResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getAddress(),
                warehouse.isActive(),
                warehouse.getCreatedAt()
        );
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        // Kalkulasi stok yang benar-benar bisa dibeli
        int available = inventory.getQuantity() - inventory.getReservedQuantity();

        return new InventoryResponse(
                inventory.getId(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getVariantId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                available,
                inventory.getUpdatedAt()
        );
    }
}