CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    stock_symbol VARCHAR(20) NOT NULL,
    action VARCHAR(20) NOT NULL CHECK(action in ('BUY', 'SELL')),
    status VARCHAR(20) NOT NULL CHECK(status in ('SUCCESS', 'PENDING', 'FAILED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    CONSTRAINT fk_transactions_wallet_id FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_transactions_stock_symbol FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol)
);
CREATE INDEX idx_transactions_stock_symbol ON transactions(stock_symbol);
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);