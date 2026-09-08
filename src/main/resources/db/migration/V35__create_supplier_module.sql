-- ─────────────────────────────────────────────────────────────────────────
-- V35: supplier module — local supplier catalog for the production quoting
--
-- Supplier      → proveedor local (aggregate root), scoped per promoter
-- CatalogItem   → precio por ítem/categoría ofrecido por el proveedor
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE suppliers (
    id             UUID         NOT NULL,
    promoter_id    UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    contact_name   VARCHAR(150),
    contact_email  VARCHAR(150),
    contact_phone  VARCHAR(50),
    service_city   VARCHAR(150),
    active         BOOLEAN      NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_suppliers          PRIMARY KEY (id),
    CONSTRAINT fk_suppliers_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE
);

CREATE INDEX idx_suppliers_promoter_id ON suppliers (promoter_id);
CREATE INDEX idx_suppliers_service_city ON suppliers (service_city);

CREATE TABLE supplier_specialties (
    supplier_id UUID        NOT NULL,
    category    VARCHAR(20) NOT NULL,

    CONSTRAINT fk_supplier_specialties_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE CASCADE,
    CONSTRAINT chk_supplier_specialties_category CHECK (category IN (
        'SOUND','LIGHTING','BACKLINE_RENTAL','STAGING','CATERING','ACCOMMODATION','TRANSPORT','SECURITY','OTHER'
    ))
);

CREATE INDEX idx_supplier_specialties_supplier_id ON supplier_specialties (supplier_id);
CREATE INDEX idx_supplier_specialties_category ON supplier_specialties (category);

CREATE TABLE supplier_catalog_items (
    id                   UUID          NOT NULL,
    supplier_id          UUID          NOT NULL,
    category             VARCHAR(20)   NOT NULL,
    item_name            VARCHAR(255)  NOT NULL,
    unit_price_amount    NUMERIC(12,2) NOT NULL,
    unit_price_currency  VARCHAR(3)    NOT NULL,
    pricing_unit         VARCHAR(20)   NOT NULL,
    lead_time_days       INTEGER,
    active               BOOLEAN       NOT NULL DEFAULT true,
    notes                TEXT,

    CONSTRAINT pk_supplier_catalog_items PRIMARY KEY (id),
    CONSTRAINT fk_catalog_items_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE CASCADE,
    CONSTRAINT chk_catalog_items_category CHECK (category IN (
        'SOUND','LIGHTING','BACKLINE_RENTAL','STAGING','CATERING','ACCOMMODATION','TRANSPORT','SECURITY','OTHER'
    )),
    CONSTRAINT chk_catalog_items_pricing_unit CHECK (pricing_unit IN ('FLAT','PER_DAY','PER_UNIT','PER_PERSON'))
);

CREATE INDEX idx_catalog_items_supplier_id ON supplier_catalog_items (supplier_id);
CREATE INDEX idx_catalog_items_category ON supplier_catalog_items (category);
