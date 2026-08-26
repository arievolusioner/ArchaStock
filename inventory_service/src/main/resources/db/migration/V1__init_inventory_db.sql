-- Extensions untuk UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABEL WAREHOUSES (MASTER GUDANG)
CREATE TABLE warehouses (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(100) NOT NULL,
                            address TEXT,
                            is_active BOOLEAN NOT NULL DEFAULT true,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                            updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. TABEL INVENTORIES (SALDO STOK SAAT INI)
CREATE TABLE inventories (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
                             variant_id UUID NOT NULL,
                             quantity INT NOT NULL DEFAULT 0,
                             reserved_quantity INT NOT NULL DEFAULT 0,
                             updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                             CONSTRAINT unique_warehouse_variant UNIQUE (warehouse_id, variant_id)
);
-- Index untuk performa saat mencari stok berdasarkan varian baju
CREATE INDEX idx_inventories_variant_id ON inventories(variant_id);

-- 3. TABEL INVENTORY_LEDGERS (BUKU BESAR MUTASI STOK)
CREATE TABLE inventory_ledgers (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   inventory_id UUID NOT NULL REFERENCES inventories(id) ON DELETE RESTRICT,
                                   movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
                                   quantity INT NOT NULL,
                                   reference_number VARCHAR(100),
                                   notes TEXT,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_ledgers_inventory_id ON inventory_ledgers(inventory_id);