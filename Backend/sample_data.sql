-- =========================
-- SAMPLE DATA INITIALIZATION
-- =========================

-- USE supermarket_db;

-- Disable foreign key checks temporarily
-- SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data (optional - be careful in production!)
-- TRUNCATE TABLE batches_exported;
-- TRUNCATE TABLE batches_imported;
-- TRUNCATE TABLE warehouse_exports_details;
-- TRUNCATE TABLE warehouse_imports_details;
-- TRUNCATE TABLE order_details;
-- TRUNCATE TABLE orders;
-- TRUNCATE TABLE batches;
-- TRUNCATE TABLE product_stores;
-- TRUNCATE TABLE product_variants;
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE warehouse_employees;
-- TRUNCATE TABLE sales_employees;
TRUNCATE TABLE employees;
-- TRUNCATE TABLE warehouses;
-- TRUNCATE TABLE stores;
-- TRUNCATE TABLE customer;

-- =========================
-- 1. CUSTOMERS
-- =========================
INSERT INTO customer (first_name, last_name, email, phone, address, loyalty_points, password) VALUES
('John', 'Doe', 'john.doe@email.com', '0123456789', '123 Main St, Ho Chi Minh City', 100, '$2a$10$6AE92.LfSdgzk9s04QHMCOkTPoWxQ7YdmNSsbCyxOWNahL6BaoyXG'),
('Jane', 'Smith', 'jane.smith@email.com', '0987654321', '456 Oak Ave, Hanoi', 250, '$2a$10$6AE92.LfSdgzk9s04QHMCOkTPoWxQ7YdmNSsbCyxOWNahL6BaoyXG'),
('Mike', 'Johnson', 'mike.johnson@email.com', '0912345678', '789 Pine Rd, Da Nang', 50, '$2a$10$6AE92.LfSdgzk9s04QHMCOkTPoWxQ7YdmNSsbCyxOWNahL6BaoyXG'),
('Emily', 'Brown', 'emily.brown@email.com', '0909876543', '321 Elm St, Ho Chi Minh City', 500, '$2a$10$6AE92.LfSdgzk9s04QHMCOkTPoWxQ7YdmNSsbCyxOWNahL6BaoyXG'),
('David', 'Wilson', 'david.wilson@email.com', '0901234567', '654 Maple Dr, Hanoi', 150, '$2a$10$6AE92.LfSdgzk9s04QHMCOkTPoWxQ7YdmNSsbCyxOWNahL6BaoyXG');

-- =========================
-- 2. EMPLOYEES
-- =========================
INSERT INTO employees (username, password, first_name, last_name, hired_at, manager_id) VALUES
-- Managers (password: manager123)
('manager1', '$2a$10$DRr9iv/1Ayb8RwboyLqEPufW.zvZF6kG0MGwmH/EySU8IZO/eZ.Vm', 'Alice', 'Manager', '2023-01-15 08:00:00', NULL),
('manager2', '$2a$10$DRr9iv/1Ayb8RwboyLqEPufW.zvZF6kG0MGwmH/EySU8IZO/eZ.Vm', 'Bob', 'Director', '2023-01-15 08:00:00', NULL),

-- Sales Employees (password: sales123)
('sales1', '$2a$10$53NPbtpph/zjpsETD0YLu.GSONg1iSULGqqmL2OSLfi.Wc.ZNpLbC', 'Tom', 'Sales', '2023-03-01 09:00:00', 1),
('sales2', '$2a$10$53NPbtpph/zjpsETD0YLu.GSONg1iSULGqqmL2OSLfi.Wc.ZNpLbC', 'Sarah', 'Sales', '2023-04-01 09:00:00', 1),
('sales3', '$2a$10$53NPbtpph/zjpsETD0YLu.GSONg1iSULGqqmL2OSLfi.Wc.ZNpLbC', 'Kevin', 'Sales', '2023-05-01 09:00:00', 2),

-- Warehouse Employees (password: warehouse123)
('warehouse1', '$2a$10$mkjI9D8ppKgau1McXju5WuUo9AqkxoWZt6rX4IMd3zmnAY..GqRra', 'Jack', 'Warehouse', '2023-02-01 08:00:00', 2),
('warehouse2', '$2a$10$mkjI9D8ppKgau1McXju5WuUo9AqkxoWZt6rX4IMd3zmnAY..GqRra', 'Lisa', 'Warehouse', '2023-03-15 08:00:00', 2),
('warehouse3', '$2a$10$mkjI9D8ppKgau1McXju5WuUo9AqkxoWZt6rX4IMd3zmnAY..GqRra', 'Mark', 'Warehouse', '2023-04-15 08:00:00', 2);

-- =========================
-- 3. STORES
-- =========================
INSERT INTO stores (name, location, manager_id) VALUES
('Downtown Store', '100 Le Loi, District 1, Ho Chi Minh City', 3),
('Uptown Store', '200 Nguyen Hue, District 1, Ho Chi Minh City', 4),
('Suburban Store', '300 Tran Hung Dao, District 5, Ho Chi Minh City', 5);

-- =========================
-- 4. WAREHOUSES
-- =========================
INSERT INTO warehouses (name, location, manager_id) VALUES
('Main Warehouse', '500 Industrial Park, District 9, Ho Chi Minh City', 6),
('Secondary Warehouse', '600 Storage Zone, Thu Duc, Ho Chi Minh City', 7),
('Northern Warehouse', '700 Logistics Area, Binh Duong', 8);

-- =========================
-- 5. SALES EMPLOYEES
-- =========================
INSERT INTO sales_employees (employee_id, store_id, total_sales) VALUES
(3, 1, 15000.00),
(4, 2, 22000.00),
(5, 3, 18500.00);

-- =========================
-- 6. WAREHOUSE EMPLOYEES
-- =========================
INSERT INTO warehouse_employees (employee_id, warehouse_id) VALUES
(6, 1),
(7, 2),
(8, 3);

-- =========================
-- 7. PRODUCTS
-- =========================
INSERT INTO products (name, description, price, sku, barcode) VALUES
-- Beverages
('Coca Cola 330ml', 'Carbonated soft drink', 1.50, 'COKE-330', '8934567890001'),
('Pepsi 330ml', 'Carbonated soft drink', 1.45, 'PEPSI-330', '8934567890002'),
('Water 500ml', 'Purified drinking water', 0.50, 'WATER-500', '8934567890003'),
('Orange Juice 1L', 'Fresh orange juice', 3.50, 'OJ-1000', '8934567890004'),
('Green Tea 500ml', 'Bottled green tea', 2.00, 'TEA-500', '8934567890005'),

-- Snacks
('Potato Chips Original', 'Crispy potato chips', 2.50, 'CHIPS-ORG', '8934567890006'),
('Chocolate Bar', 'Milk chocolate bar', 1.80, 'CHOCO-BAR', '8934567890007'),
('Cookies Pack', 'Assorted cookies', 3.00, 'COOKIE-PK', '8934567890008'),
('Nuts Mix 200g', 'Mixed nuts pack', 4.50, 'NUTS-200', '8934567890009'),
('Candy Bag', 'Assorted candies', 2.20, 'CANDY-BAG', '8934567890010'),

-- Dairy
('Fresh Milk 1L', 'Fresh dairy milk', 3.20, 'MILK-1L', '8934567890011'),
('Yogurt 4-pack', 'Fruit yogurt pack', 4.00, 'YOGURT-4', '8934567890012'),
('Cheese Block 200g', 'Cheddar cheese', 5.50, 'CHEESE-200', '8934567890013'),
('Butter 250g', 'Salted butter', 4.80, 'BUTTER-250', '8934567890014'),

-- Instant Food
('Instant Noodles', 'Chicken flavor noodles', 1.20, 'NOODLE-CHK', '8934567890015'),
('Cup Noodles', 'Beef flavor cup noodles', 1.50, 'CUP-BEEF', '8934567890016'),
('Instant Rice', 'Ready to eat rice', 2.80, 'RICE-INST', '8934567890017'),

-- Household
('Laundry Detergent 1kg', 'Washing powder', 6.50, 'DETERG-1K', '8934567890018'),
('Dish Soap 500ml', 'Dishwashing liquid', 3.50, 'SOAP-500', '8934567890019'),
('Toilet Paper 12-pack', 'Soft toilet tissue', 8.00, 'TP-12', '8934567890020');

-- =========================
-- 8. PRODUCT VARIANTS
-- =========================
INSERT INTO product_variants (product_id, variant_json) VALUES
-- Coca Cola variants
(1, '{"Size": "330ml", "Package": "Can"}'),
(1, '{"Size": "500ml", "Package": "Bottle"}'),
(1, '{"Size": "1.5L", "Package": "Bottle"}'),

-- Pepsi variants
(2, '{"Size": "330ml", "Package": "Can"}'),
(2, '{"Size": "500ml", "Package": "Bottle"}'),

-- Chips variants
(6, '{"Flavor": "Original", "Size": "50g"}'),
(6, '{"Flavor": "BBQ", "Size": "50g"}'),
(6, '{"Flavor": "Sour Cream", "Size": "50g"}'),

-- Chocolate variants
(7, '{"Type": "Milk Chocolate", "Size": "50g"}'),
(7, '{"Type": "Dark Chocolate", "Size": "50g"}'),

-- Instant Noodles variants
(15, '{"Flavor": "Chicken", "Spicy": "No"}'),
(15, '{"Flavor": "Chicken", "Spicy": "Yes"}'),
(15, '{"Flavor": "Beef", "Spicy": "No"}'),
(15, '{"Flavor": "Seafood", "Spicy": "Yes"}');

-- =========================
-- 9. BATCHES
-- =========================
INSERT INTO batches (variant_id, product_id, warehouse_id, manufacture, supplier, quantity_total, quantity_available, create_date, expiry_date) VALUES
-- Coca Cola batches
(1, 1, 1, 'Coca Cola Company', 'Coca Cola Vietnam', 2000, 1850, '2024-01-15', '2025-06-15'),
(2, 1, 1, 'Coca Cola Company', 'Coca Cola Vietnam', 1500, 1320, '2024-02-01', '2025-07-01'),
(3, 1, 2, 'Coca Cola Company', 'Coca Cola Vietnam', 1000, 950, '2024-01-20', '2025-06-20'),

-- Pepsi batches
(4, 2, 1, 'PepsiCo', 'Pepsico Vietnam', 1800, 1600, '2024-01-10', '2025-05-10'),
(5, 2, 2, 'PepsiCo', 'Pepsico Vietnam', 1200, 1100, '2024-02-05', '2025-06-05'),

-- Water batches
(1, 3, 1, 'AquaViet', 'Local Distributor', 5000, 4500, '2024-03-01', '2026-03-01'),
(1, 3, 2, 'AquaViet', 'Local Distributor', 3000, 2800, '2024-03-10', '2026-03-10'),

-- Chips batches
(6, 6, 1, 'Lays Company', 'Snack Distributor', 800, 650, '2024-02-15', '2024-08-15'),
(7, 6, 1, 'Lays Company', 'Snack Distributor', 600, 520, '2024-02-20', '2024-08-20'),

-- Instant Noodles batches
(11, 15, 1, 'Acecook', 'Food Supplier Ltd', 3000, 2750, '2024-01-05', '2024-12-05'),
(12, 15, 2, 'Acecook', 'Food Supplier Ltd', 2500, 2300, '2024-01-15', '2024-12-15'),
(13, 15, 1, 'Acecook', 'Food Supplier Ltd', 2000, 1850, '2024-02-01', '2025-01-01'),

-- Dairy products (shorter expiry)
(1, 11, 1, 'Vinamilk', 'Dairy Co', 1000, 800, '2024-06-01', '2024-06-20'),
(1, 12, 1, 'Vinamilk', 'Dairy Co', 800, 650, '2024-06-05', '2024-06-25'),

-- Household items (long expiry)
(1, 18, 3, 'P&G Vietnam', 'Household Supplier', 500, 480, '2024-01-01', '2026-01-01'),
(1, 19, 3, 'Unilever', 'Household Supplier', 600, 570, '2024-01-10', '2026-01-10'),
(1, 20, 3, 'Kimberly Clark', 'Household Supplier', 400, 350, '2024-02-01', '2026-02-01');

-- =========================
-- 10. PRODUCT STORES (Store Inventory)
-- =========================
INSERT INTO product_stores (product_id, store_id, quantity_in_stock) VALUES
-- Downtown Store (Store 1)
(1, 1, 150), (2, 1, 120), (3, 1, 200), (4, 1, 80), (5, 1, 100),
(6, 1, 60), (7, 1, 75), (8, 1, 50), (9, 1, 40), (10, 1, 65),
(11, 1, 45), (12, 1, 55), (15, 1, 200), (16, 1, 150),

-- Uptown Store (Store 2)
(1, 2, 180), (2, 2, 140), (3, 2, 250), (4, 2, 90), (5, 2, 110),
(6, 2, 70), (7, 2, 85), (11, 2, 50), (12, 2, 60), (15, 2, 180),
(18, 2, 30), (19, 2, 40), (20, 2, 25),

-- Suburban Store (Store 3)
(1, 3, 120), (2, 3, 100), (3, 3, 180), (6, 3, 50), (7, 3, 60),
(10, 3, 55), (15, 3, 150), (16, 3, 120), (18, 3, 35), (19, 3, 45);

-- =========================
-- 11. ORDERS
-- =========================
INSERT INTO orders (customer_id, created_at, status, total_money) VALUES
-- Recent orders (Last 7 days)
(1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Completed', 25.50),
(2, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Completed', 48.90),
(3, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Processing', 32.40),
(4, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Completed', 67.80),
(5, DATE_SUB(NOW(), INTERVAL 4 DAY), 'Completed', 19.50),

-- Older orders (Last 30 days)
(1, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Completed', 31.20),
(2, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Completed', 44.50),
(3, DATE_SUB(NOW(), INTERVAL 18 DAY), 'Completed', 28.70),
(4, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Completed', 55.30),
(5, DATE_SUB(NOW(), INTERVAL 25 DAY), 'Completed', 38.90),

-- Pending orders
(1, NOW(), 'Pending', 42.00),
(3, NOW(), 'Pending', 23.50),
(5, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'Processing', 51.20);

-- =========================
-- 12. ORDER DETAILS
-- =========================
INSERT INTO order_details (order_id, product_id, quantity, sub_total) VALUES
-- Order 1 (Customer 1)
(1, 1, 5, 7.50),
(1, 6, 3, 7.50),
(1, 15, 8, 9.60),

-- Order 2 (Customer 2)
(2, 2, 10, 14.50),
(2, 7, 5, 9.00),
(2, 11, 8, 25.60),

-- Order 3 (Customer 3) - Processing
(3, 3, 20, 10.00),
(3, 8, 5, 15.00),
(3, 10, 3, 6.60),

-- Order 4 (Customer 4)
(4, 4, 12, 42.00),
(4, 9, 4, 18.00),
(4, 12, 2, 8.00),

-- Order 5 (Customer 5)
(5, 5, 5, 10.00),
(5, 15, 8, 9.60),

-- Order 6 (Customer 1)
(6, 1, 8, 12.00),
(6, 6, 4, 10.00),
(6, 16, 6, 9.00),

-- Order 7 (Customer 2)
(7, 2, 12, 17.40),
(7, 11, 8, 25.60),

-- Order 8 (Customer 3)
(8, 7, 10, 18.00),
(8, 8, 3, 9.00),

-- Order 9 (Customer 4)
(9, 11, 12, 38.40),
(9, 12, 4, 16.00),

-- Order 10 (Customer 5)
(10, 1, 10, 15.00),
(10, 15, 20, 24.00),

-- Order 11 (Customer 1) - Pending
(11, 2, 15, 21.75),
(11, 6, 8, 20.00),

-- Order 12 (Customer 3) - Pending
(12, 3, 30, 15.00),
(12, 5, 4, 8.00),

-- Order 13 (Customer 5) - Processing
(13, 4, 10, 35.00),
(13, 9, 3, 13.50);

-- =========================
-- 13. WAREHOUSE IMPORTS
-- =========================
INSERT INTO warehouse_imports_details (supplier, import_date, unit_price, employee_import, warehouse_id) VALUES
('Coca Cola Vietnam', DATE_SUB(NOW(), INTERVAL 60 DAY), 1.20, 6, 1),
('Pepsico Vietnam', DATE_SUB(NOW(), INTERVAL 55 DAY), 1.15, 6, 1),
('Snack Distributor', DATE_SUB(NOW(), INTERVAL 50 DAY), 2.00, 7, 2),
('Food Supplier Ltd', DATE_SUB(NOW(), INTERVAL 45 DAY), 0.95, 6, 1),
('Dairy Co', DATE_SUB(NOW(), INTERVAL 5 DAY), 2.80, 7, 1),
('Household Supplier', DATE_SUB(NOW(), INTERVAL 90 DAY), 5.50, 8, 3);

-- =========================
-- 14. WAREHOUSE EXPORTS
-- =========================
INSERT INTO warehouse_exports_details (reason, employee_export, export_date, warehouse_id) VALUES
('Transfer to Store 1', 6, DATE_SUB(NOW(), INTERVAL 30 DAY), 1),
('Transfer to Store 2', 7, DATE_SUB(NOW(), INTERVAL 25 DAY), 2),
('Transfer to Store 3', 8, DATE_SUB(NOW(), INTERVAL 20 DAY), 3),
('Transfer to Store 1', 6, DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
('Damaged goods removal', 7, DATE_SUB(NOW(), INTERVAL 10 DAY), 2),
('Transfer to Store 2', 6, DATE_SUB(NOW(), INTERVAL 5 DAY), 1);

-- =========================
-- 15. BATCHES IMPORTED
-- =========================
INSERT INTO batches_imported (batch_import_id, import_details_id, quantity) VALUES
(1, 1, 2000),
(2, 1, 1500),
(4, 2, 1800),
(6, 3, 800),
(11, 4, 3000),
(14, 5, 1000),
(16, 6, 500);

-- =========================
-- 16. BATCHES EXPORTED
-- =========================
TRUNCATE TABLE batches_exported;

INSERT INTO batches_exported (batch_export_id, export_details_id, quantity) VALUES
(1, 1, 150),
(4, 1, 100),
(2, 2, 180),
(5, 2, 120),
(11, 3, 150),
(1, 4, 100),
(6, 5, 50),
(2, 6, 140);

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- VERIFICATION QUERIES
-- =========================

-- Check data counts
SELECT 'Customers' AS table_name, COUNT(*) AS count FROM customer
UNION ALL
SELECT 'Employees', COUNT(*) FROM employees
UNION ALL
SELECT 'Stores', COUNT(*) FROM stores
UNION ALL
SELECT 'Warehouses', COUNT(*) FROM warehouses
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Product Variants', COUNT(*) FROM product_variants
UNION ALL
SELECT 'Batches', COUNT(*) FROM batches
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Details', COUNT(*) FROM order_details;

-- =========================
-- END OF SAMPLE DATA
-- =========================