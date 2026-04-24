CREATE TABLE IF NOT EXISTS wallets_inventory (
    wallet_id BIGINT,
    stock_symbol VARCHAR(20),
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    PRIMARY KEY (wallet_id, stock_symbol),
    CONSTRAINT fk_inventory_wallet_id FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    CONSTRAINT fk_wallets_inventory_symbol FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol) ON DELETE CASCADE
);
CREATE INDEX idx_wallets_inventory_symbol ON wallets_inventory(stock_symbol);