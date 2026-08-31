-- Extensions untuk UUID & JSONB Ops (biasanya sudah default aktif di PG 13+)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABEL CATEGORIES
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(100) NOT NULL,
                            slug VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                            updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. TABEL PRODUCTS (MASTER PARENT)
CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          brand VARCHAR(100),
                          is_active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. TABEL PRODUCT_VARIANTS (AKTOR TRANSAKSI & KATALOG DETAIL)
CREATE TABLE product_variants (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                  sku VARCHAR(100) NOT NULL UNIQUE,
                                  barcode VARCHAR(100),
                                  price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                                  cost_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                                  weight_grams INT DEFAULT 0,
                                  attributes JSONB NOT NULL DEFAULT '{}'::jsonb, -- 🔑 Fleksibel (Fashion: size/color, HP: ram/storage)
                                  is_active BOOLEAN NOT NULL DEFAULT true,
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- INDEXING UNTUK PERFORMA QUERY
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_product_variants_product ON product_variants(product_id);
CREATE INDEX idx_product_variants_sku ON product_variants(sku);
-- GIN Index khusus kolom JSONB agar query pencarian atribut sangat kencang
CREATE INDEX idx_product_variants_attributes ON product_variants USING gin (attributes);