INSERT INTO stocks (symbol, name)
VALUES ('AAPL', 'Apple Inc.'),
    ('GOOG', 'Alphabet Inc.'),
    ('MSFT', 'Microsoft Corporation') ON CONFLICT (symbol) DO NOTHING;