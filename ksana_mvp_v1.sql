-- =====================================================
-- KSANA MVP DATABASE SCRIPT v1.0
-- PostgreSQL 15+
-- Purpose: First MVP (Delivery + Pickup + Barcode)
-- =====================================================

BEGIN;

-- =====================================================
-- 1. CREATE SCHEMAS
-- =====================================================
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS orders;
CREATE SCHEMA IF NOT EXISTS logistics;

-- =====================================================
-- 2. CORE SCHEMA
-- =====================================================

-- USERS
CREATE TABLE IF NOT EXISTS core.users (
    user_id BIGSERIAL PRIMARY KEY,
    mobile_number VARCHAR(15) UNIQUE NOT NULL,
    name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ADDRESSES
CREATE TABLE IF NOT EXISTS core.addresses (
    address_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES core.users(user_id),
    house VARCHAR(255),
    area VARCHAR(255),
    pincode VARCHAR(10),
    landmark VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VENDOR / STORE
CREATE TABLE IF NOT EXISTS core.vendors (
    vendor_id BIGSERIAL PRIMARY KEY,
    vendor_name VARCHAR(100) NOT NULL,
    vendor_type VARCHAR(20) DEFAULT 'INTERNAL',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CATEGORY
CREATE TABLE IF NOT EXISTS core.categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- PRODUCT (WITH BARCODE)
CREATE TABLE IF NOT EXISTS core.products (
    product_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id BIGINT REFERENCES core.categories(category_id),
    barcode VARCHAR(100) UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PRODUCT PRICE HISTORY
CREATE TABLE IF NOT EXISTS core.product_price_history (
    price_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES core.products(product_id),
    price NUMERIC(10,2) NOT NULL,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. INVENTORY SCHEMA
-- =====================================================

CREATE TABLE IF NOT EXISTS inventory.inventory (
    inventory_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES core.products(product_id),
    vendor_id BIGINT REFERENCES core.vendors(vendor_id),
    stock_quantity INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory.inventory_log (
    log_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES core.products(product_id),
    vendor_id BIGINT REFERENCES core.vendors(vendor_id),
    change_quantity INT NOT NULL,
    reason VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 4. ORDERS SCHEMA
-- =====================================================

CREATE TABLE IF NOT EXISTS orders.orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES core.users(user_id),
    address_id BIGINT REFERENCES core.addresses(address_id),
    fulfillment_type VARCHAR(20)
        CHECK (fulfillment_type IN ('DELIVERY','PICKUP')),
    total_amount NUMERIC(10,2),
    order_status VARCHAR(30),
    payment_status VARCHAR(30),
    tracking_token VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders.order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders.orders(order_id),
    product_id BIGINT REFERENCES core.products(product_id),
    vendor_id BIGINT REFERENCES core.vendors(vendor_id),
    quantity INT NOT NULL,
    final_price NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS orders.order_status_log (
    log_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders.orders(order_id),
    status VARCHAR(30),
    changed_by VARCHAR(20),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders.payments (
    payment_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders.orders(order_id),
    payment_method VARCHAR(30),
    payment_status VARCHAR(30),
    transaction_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5. LOGISTICS SCHEMA
-- =====================================================

CREATE TABLE IF NOT EXISTS logistics.pickup_locations (
    pickup_location_id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT REFERENCES core.vendors(vendor_id),
    name VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS logistics.deliveries (
    delivery_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders.orders(order_id),
    assigned_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- =====================================================
-- 6. BASIC SEED DATA
-- =====================================================

INSERT INTO core.vendors (vendor_name)
VALUES ('KSANA MAIN STORE')
ON CONFLICT DO NOTHING;

COMMIT;

-- =====================================================
-- END OF KSANA MVP DATABASE SCRIPT v1.0
-- =====================================================
