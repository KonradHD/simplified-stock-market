CREATE TABLE IF NOT EXISTS stocks (
    symbol VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    price NUMERIC(15, 4) NOT NULL DEFAULT 1 CHECK(price = 1) -- price is fixed
);