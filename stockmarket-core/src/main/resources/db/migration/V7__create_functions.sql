CREATE OR REPLACE FUNCTION trg_init_bank_inventory() RETURNS TRIGGER AS $$ BEGIN
INSERT INTO bank_inventory (stock_symbol, quantity)
VALUES (NEW.symbol, 0) ON CONFLICT (stock_symbol) DO NOTHING;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;