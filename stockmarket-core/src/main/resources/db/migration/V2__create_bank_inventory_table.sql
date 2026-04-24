CREATE TABLE IF NOT EXISTS bank_inventory (
    stock_symbol VARCHAR(20) PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK(quantity >= 0),
    CONSTRAINT fk_bank_inventory_symbol FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol) ON DELETE CASCADE
);