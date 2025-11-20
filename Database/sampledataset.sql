 USE supermarket_db;
INSERT INTO stores (id, name, location)
VALUES
(1, 'Supermart District 1', 'HCM - District 1'),
(2, 'Supermart District 7', 'HCM - District 7');

INSERT INTO warehouses (id, name, location)
VALUES
(2, 'Central Warehouse', 'HCM - Thu Duc'),
(3, 'South Warehouse', 'HCM - Binh Chanh');


INSERT INTO employees(id, username, password, first_name, last_name)
VALUES
(1, 'manager1', 'pass', 'Alice', 'Nguyen'),
(2, 'sale1', 'pass', 'Bob', 'Tran'),
(3, 'ware1', 'pass', 'Charlie', 'Le');

INSERT INTO sales_employees (employee_id, store_id, total_sales)
VALUES
(2, 1, 0);

INSERT INTO warehouse_employees (employee_id, warehouse_id)
VALUES
(3, 1);

UPDATE stores SET manager_id = 2 WHERE id = 1;
UPDATE warehouses SET manager_id = 3 WHERE id = 1;

INSERT INTO products (id, name, description, price, SKU)
VALUES
(1, 'Milk 1L', 'Fresh milk', 30000, 'MLK-1L'),
(2, 'Instant Noodles', 'Beef flavor', 7000, 'NOOD-BF'),
(3, 'Shampoo 500ml', 'Herbal shampoo', 65000, 'SHMP-500');

INSERT INTO product_variants (id, product_id, attribute, value)
VALUES
(1, 1, 'Fat', 'Low-fat'),
(2, 1, 'Package', 'Bottle'),
(3, 2, 'Size', '80g'),
(4, 3, 'Scent', 'Herbal');

INSERT INTO warehouse_imports_details(details_id, supplier, unit_price, employee_import, warehouse_id)
VALUES
(1, 'DairyCorp', 25000, 3, 1),
(2, 'FoodSupply', 5000, 3, 1);

INSERT INTO batches_imported(batch_import_id, import_details_id, quantity)
VALUES
(1001, 1, 100),
(1002, 2, 300);

INSERT INTO batches
(id, product_id, warehouse_id, manufacture, supplier, quantity_total, quantity_available, create_date, expiry_date)
VALUES
(1, 1, 1, 'Vinamilk', 'DairyCorp', 100, 100, '2025-01-01', '2025-12-31'),
(2, 2, 1, 'AceCook', 'FoodSupply', 300, 300, '2025-01-10', '2026-01-10');

INSERT INTO product_stores (product_id, store_id, quantity_in_stock)
VALUES
(1, 1, 20),
(2, 1, 50),
(3, 1, 15);

INSERT INTO customers(id, first_name, last_name, email, phone, address)
VALUES
(1, 'Tien', 'Pham', 'tien@example.com', '0900000000', 'HCM');

INSERT INTO carts(id, customer_id)
VALUES
(1, 1);

INSERT INTO cart_items (cart_id, product_id, quantity, sub_total)
VALUES
(1, 1, 2, 60000),
(1, 2, 5, 35000);

INSERT INTO orders (id, customer_id, total_money)
VALUES
(1, 1, 95000);

INSERT INTO order_details(order_id, product_id, quantity, sub_total)
VALUES
(1, 1, 2, 60000),
(1, 2, 5, 35000);
