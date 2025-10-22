-- =========================
-- DATABASE SCHEMA (MySQL)
-- =========================
CREATE DATABASE IF NOT EXISTS supermarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE supermarket_db;

CREATE TABLE customers (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(100),
last_name VARCHAR(100),
email VARCHAR(255) UNIQUE NOT NULL,
phone VARCHAR(20),
address TEXT,
loyalty_points INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE carts (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
customer_id BIGINT NOT NULL UNIQUE,
created_at TIMESTAMP NOT NULL DEFAULT NOW(),
updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
CONSTRAINT fk_carts_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
description TEXT,
price DECIMAL(10,2) NOT NULL,
SKU VARCHAR(100) UNIQUE,
image VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart_items (
cart_id BIGINT NOT NULL,
product_id BIGINT NOT NULL,
quantity INT NOT NULL DEFAULT 1,
sub_total DECIMAL(12,2) NOT NULL,
PRIMARY KEY (cart_id, product_id),
CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
customer_id BIGINT NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT NOW(),
status VARCHAR(50) NOT NULL DEFAULT 'Pending',
total_money DECIMAL(15,2) NOT NULL,
CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_details (
order_id BIGINT NOT NULL,
product_id BIGINT NOT NULL,
quantity INT NOT NULL,
sub_total DECIMAL(12,2) NOT NULL,
PRIMARY KEY (order_id, product_id),
CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES orders(id),
CONSTRAINT fk_order_details_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_variants (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
product_id BIGINT NOT NULL,
attribute VARCHAR(100) NOT NULL COMMENT 'Ví dụ: Color, Size',
value VARCHAR(100) NOT NULL COMMENT 'Ví dụ: Red, S, M, L',
CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stores (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
location TEXT,
manager_id BIGINT UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE warehouses (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
location TEXT,
manager_id BIGINT UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE batches (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
product_id BIGINT NOT NULL,
warehouse_id BIGINT NOT NULL,
manufacture VARCHAR(255),
supplier VARCHAR(255),
quantity_total INT NOT NULL,
quantity_available INT NOT NULL,
create_date DATE,
expiry_date DATE,
CONSTRAINT fk_batches_product FOREIGN KEY (product_id) REFERENCES products(id),
CONSTRAINT fk_batches_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_stores (
product_id BIGINT NOT NULL,
store_id BIGINT NOT NULL,
quantity_in_stock INT NOT NULL DEFAULT 0,
PRIMARY KEY (product_id, store_id),
CONSTRAINT fk_product_stores_product FOREIGN KEY (product_id) REFERENCES products(id),
CONSTRAINT fk_product_stores_store FOREIGN KEY (store_id) REFERENCES stores(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE employees (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
first_name VARCHAR(100),
last_name VARCHAR(100),
hired_at TIMESTAMP DEFAULT NOW(),
manager_id BIGINT,
CONSTRAINT fk_employees_manager FOREIGN KEY (manager_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sales_employees (
employee_id BIGINT PRIMARY KEY,
store_id BIGINT,
total_sales DECIMAL(15,2) DEFAULT 0,
CONSTRAINT fk_sales_employees_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
CONSTRAINT fk_sales_employees_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE warehouse_employees (
employee_id BIGINT PRIMARY KEY,
warehouse_id BIGINT,
CONSTRAINT fk_warehouse_employees_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
CONSTRAINT fk_warehouse_employees_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE stores
ADD CONSTRAINT fk_stores_manager FOREIGN KEY (manager_id) REFERENCES sales_employees(employee_id) ON DELETE SET NULL;

ALTER TABLE warehouses
ADD CONSTRAINT fk_warehouses_manager FOREIGN KEY (manager_id) REFERENCES warehouse_employees(employee_id) ON DELETE SET NULL;

CREATE TABLE batches_imported (
    batch_import_id BIGINT PRIMARY KEY ,
    import_details_id BIGINT NOT NULL ,
    quantity INT NOT NULL,
    CONSTRAINT fk_import_details FOREIGN KEY (import_details_id) REFERENCES warehouse_imports_details(details_id)
) ENGINE = InnoDB;

CREATE TABLE batches_exported(
    batch_export_id BIGINT PRIMARY KEY ,
    export_details_id BIGINT NOT NULL ,
    quantity INT NOT NULL,
    CONSTRAINT FOREIGN KEY (export_details_id) REFERENCES warehouse_exports_details(details_id)
) ENGINE = InnoDB;

CREATE TABLE warehouse_exports_details (
details_id BIGINT PRIMARY KEY AUTO_INCREMENT,
reason VARCHAR(255),
employee_export BIGINT,
export_date TIMESTAMP NOT NULL DEFAULT NOW(),
warehouse_id bigint not null,
CONSTRAINT FOREIGN KEY (employee_export) REFERENCES warehouse_employees(employee_id) ON DELETE RESTRICT,
CONSTRAINT FOREIGN KEY (warehouse_id) REFERENCES  warehouses(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE warehouse_imports_details (
details_id BIGINT PRIMARY KEY AUTO_INCREMENT,
supplier VARCHAR(255),
import_date TIMESTAMP NOT NULL DEFAULT NOW(),
unit_price DECIMAL(10,2) NOT NULL,
employee_import BIGINT,
warehouse_id bigint not null,
CONSTRAINT FOREIGN KEY (employee_import) REFERENCES  warehouse_employees(employee_id) ON DELETE RESTRICT,
CONSTRAINT FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- END OF DATABASE SCHEMA