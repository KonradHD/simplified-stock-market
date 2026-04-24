CREATE TABLE IF NOT EXISTS logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    stock_symbol VARCHAR(20) NOT NULL,
    action_type VARCHAR(20) NOT NULL CHECK(
        action_type in (
            'WALLET_CREATE',
            'WALLET_DELETE',
            'TRANSACTION_BUY',
            'TRANSACTION_SELL'
        )
    ),
    status VARCHAR(20) NOT NULL CHECK(status in ('INFO', 'ERROR', 'WARN')),
    info VARCHAR(255) NOT NULL,
    quantity INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_logs_wallet_id FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_logs_symbol FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol)
);
CREATE INDEX idx_logs_wallet_id ON logs(wallet_id);
CREATE INDEX idx_logs_action_type ON logs(action_type);