use supermarket_db;

-- Nhân viên
INSERT INTO employees(username, password, first_name, last_name)
VALUES ('nv_test','pass','Nguyen','Van A');

-- Kho
INSERT INTO warehouses(name, location) VALUES ('Kho HCM', 'HCM');

INSERT INTO warehouse_employees VALUES(1,1);

-- Lấy id để test
SET @employee_id = (SELECT id FROM employees LIMIT 1);
SET @warehouse_id = (SELECT id FROM warehouses LIMIT 1);
SET @supplier = 'Vinamilk';
SET @unit_price = 20000;

SET @test_json = '[
  {
    "product_id": 1,
    "product_name": "Sữa Vinamilk 330ml",
    "description": "Sữa tiệt trùng 330ml",
    "sku": "VINAMILK-330",
    "barcode": "8931234567330",
    "variant_json": {"Size":"330ml","Pack":"Hộp"},
    "quantity": 50,
    "manufacture": "2025-10-01",
    "expiry_date": "2026-04-01"
  },
  {
    "product_id": 1,
    "product_name": "Sữa Vinamilk 500ml",
    "description": "Sữa tiệt trùng 500ml",
    "sku": "VINAMILK-500",
    "barcode": "8931234567500",
    "variant_json": {"Size":"500ml","Pack":"Hộp"},
    "quantity": 30,
    "manufacture": "2025-10-05",
    "expiry_date": "2026-04-05"
  },
  {
    "product_id": 2,
    "product_name": "Nước ép cam 250ml",
    "description": "Nước ép cam nguyên chất 250ml",
    "sku": "ORANGE-JUICE-250",
    "barcode": "8938888888250",
    "variant_json": {"Size":"250ml","Pack":"Chai"},
    "quantity": 20,
    "manufacture": "2025-10-10",
    "expiry_date": "2026-03-10"
  }
]';


CALL sp_create_import(@employee_id, @warehouse_id, @supplier, @unit_price, @import_id);
SELECT @import_id;
SELECT * FROM warehouse_imports_details WHERE details_id = @import_id;

CALL sp_insert_products(@test_json, @unit_price);
SELECT * FROM products;

CALL sp_insert_variants(@test_json);
SELECT * FROM product_variants;

-- Giả lập batch đã có sẵn để test update
INSERT INTO batches(variant_id, product_id, warehouse_id, supplier, quantity_total, quantity_available, create_date)
SELECT id, product_id, @warehouse_id, @supplier, 10, 10, NOW()
FROM product_variants
WHERE id = 1;

CALL sp_update_batches(@test_json, @warehouse_id);
SELECT * FROM batches;

CALL sp_insert_new_batches(@test_json, @warehouse_id, @supplier);
SELECT * FROM batches;

CALL sp_log_batches_imported(@test_json, @warehouse_id, @import_id);
SELECT * FROM batches_imported;




SET @export_reason = 'Xuất bán hàng';
CALL sp_create_export(@employee_id, @warehouse_id, @export_reason, @export_id);
SELECT * FROM warehouse_exports_details WHERE details_id = @export_id;

-- =============================
-- 2️⃣ Lấy batch để xuất
-- =============================
CALL sp_select_batches_for_export(@warehouse_id, 0, 0); -- tất cả sản phẩm và variant
-- Chọn 1 batch để test log
SET @batch_id_1 = (SELECT id FROM batches WHERE warehouse_id = @warehouse_id LIMIT 1);
SELECT @batch_id_1;
-- =============================
-- 3️⃣ Ghi log batch xuất
-- =============================
-- Xuất 5 sản phẩm từ batch đầu tiên
CALL sp_log_batches_exported(@export_id, @batch_id_1, 5);
-- Xuất tiếp batch thứ 2 nếu có
SET @batch_id_2 = (SELECT id FROM batches WHERE warehouse_id = @warehouse_id AND id != @batch_id_1 LIMIT 1);
SELECT id FROM batches WHERE warehouse_id = @warehouse_id AND id != @batch_id_1 LIMIT 1;
-- Xem giá trị batch_id_2
SELECT @batch_id_2 AS batch_id_2;
CALL sp_log_batches_exported(@export_id, @batch_id_2, 10);

-- Kiểm tra trạng thái batch sau khi xuất
SELECT * FROM batches WHERE warehouse_id = @warehouse_id;

-- Kiểm tra log batch xuất
SELECT * FROM batches_exported WHERE export_details_id = @export_id;

-- =============================
-- 4️⃣ Xuất JSON tổng hợp
-- =============================
CALL sp_export_warehouse_batches(@export_id);
-- =============================
-- 4. Test fn_variant_exists
-- =============================

-- Kiểm tra tồn tại variant theo warehouse và filter JSON
SELECT fn_variant_exists("Sữa Vinamilk", '{"Size":"330ml"}', 1) AS exists_330ml;
SELECT fn_variant_exists("Sữa Vinamilk", '{"Size":"500ml"}', 1) AS exists_500ml;
SELECT fn_variant_exists("Sữa Vinamilk", '{"Size":"1L"}', 1) AS exists_1L;  -- FALSE

-- Tắt kiểm tra FK
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu từ tất cả bảng
TRUNCATE TABLE batches_imported;
TRUNCATE TABLE batches_exported;
TRUNCATE TABLE warehouse_exports_details;
TRUNCATE TABLE warehouse_imports_details;
TRUNCATE TABLE batches;
TRUNCATE TABLE product_variants;
TRUNCATE TABLE products;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_details;
TRUNCATE TABLE customers;
TRUNCATE TABLE product_stores;
TRUNCATE TABLE sales_employees;
TRUNCATE TABLE warehouse_employees;
TRUNCATE TABLE employees;
TRUNCATE TABLE stores;
TRUNCATE TABLE warehouses;

-- Bật lại kiểm tra FK
SET FOREIGN_KEY_CHECKS = 1;
