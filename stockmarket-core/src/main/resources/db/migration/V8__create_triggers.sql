CREATE TRIGGER after_stock_insert
AFTER
INSERT ON stocks FOR EACH ROW EXECUTE FUNCTION trg_init_bank_inventory();