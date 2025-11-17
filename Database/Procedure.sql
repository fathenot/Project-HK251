USE supermarket_db;
DELIMITER $$
USE supermarket_db;
DELIMITER $$

CREATE PROCEDURE sp_import_goods(
    IN p_employee_id BIGINT,
    IN p_warehouse_id BIGINT,
    IN p_supplier VARCHAR(255),
    IN p_unit_price DECIMAL(10,2),
    IN p_items_json JSON  -- [{"product_id":101,"product_name":"Bánh quy","variant_json":{"Size":"M","Color":"Red"},"quantity":50}, ...]
)
BEGIN
    DECLARE v_import_id BIGINT;
    DECLARE v_variant_id BIGINT;
    DECLARE v_product_id BIGINT;
    DECLARE v_product_name VARCHAR(255);
    DECLARE v_variant_json JSON;
    DECLARE v_quantity INT;
    DECLARE v_batch_id BIGINT;
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_exists INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error during import';
    END;

    START TRANSACTION;

    -- 1. Ghi phiếu nhập
    INSERT INTO warehouse_imports_details(employee_import, warehouse_id, supplier, unit_price, import_date)
    VALUES (p_employee_id, p_warehouse_id, p_supplier, p_unit_price, NOW());

    SET v_import_id = LAST_INSERT_ID();

    -- 2. Duyệt JSON nhập
    DECLARE cur CURSOR FOR
    SELECT 
        product_id, 
        product_name, 
        variant_json, 
        quantity
    FROM JSON_TABLE(p_items_json, '$[*]'
        COLUMNS (
            product_id BIGINT PATH '$.product_id',
            product_name VARCHAR(255) PATH '$.product_name',
            variant_json JSON PATH '$.variant_json',
            quantity INT PATH '$.quantity'
        )) AS jt;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_product_id, v_product_name, v_variant_json, v_quantity;
        IF v_done THEN LEAVE read_loop; END IF;

        -- 2a. Tạo product nếu chưa tồn tại
        SELECT COUNT(*) INTO v_exists FROM products WHERE id = v_product_id;
        IF v_exists = 0 THEN
            INSERT INTO products(id, name, price) VALUES (v_product_id, v_product_name, p_unit_price);
        END IF;

        -- 2b. Kiểm tra variant đã tồn tại chưa
        SELECT id INTO v_variant_id 
        FROM product_variants 
        WHERE product_id = v_product_id AND variant_json = v_variant_json
        LIMIT 1;

        IF v_variant_id IS NULL THEN
            -- Tạo variant mới
            INSERT INTO product_variants(product_id, variant_json) VALUES (v_product_id, v_variant_json);
            SET v_variant_id = LAST_INSERT_ID();
        END IF;

        -- 2c. Kiểm tra batch tồn tại chưa trong kho
        SELECT id INTO v_batch_id
        FROM batches
        WHERE variant_id = v_variant_id AND warehouse_id = p_warehouse_id
        LIMIT 1;

        IF v_batch_id IS NOT NULL THEN
            -- Merge batch
            UPDATE batches
            SET quantity_total = quantity_total + v_quantity,
                quantity_available = quantity_available + v_quantity
            WHERE id = v_batch_id;
        ELSE
            -- Tạo batch mới
            INSERT INTO batches(variant_id, product_id, warehouse_id, supplier, quantity_total, quantity_available, create_date)
            VALUES (v_variant_id, v_product_id, p_warehouse_id, p_supplier, v_quantity, v_quantity, NOW());

            SET v_batch_id = LAST_INSERT_ID();
        END IF;

        -- Ghi log batch nhập
        INSERT INTO batches_imported(batch_import_id, import_details_id, quantity)
        VALUES (v_batch_id, v_import_id, v_quantity);

    END LOOP;

    CLOSE cur;

    COMMIT;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_export_goods(
    IN p_employee_id BIGINT,
    IN p_warehouse_id BIGINT,
    IN p_items_json JSON, -- [{"variant_id":1,"quantity":20}, ...]
    IN p_reason VARCHAR(255)
)
BEGIN
    DECLARE v_export_id BIGINT;
    DECLARE v_variant_id BIGINT;
    DECLARE v_product_id BIGINT;
    DECLARE v_quantity INT;
    DECLARE v_done INT DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error during export';
    END;

    START TRANSACTION;

    -- 1. Tạo phiếu xuất
    INSERT INTO warehouse_exports_details(employee_export, warehouse_id, reason, export_date)
    VALUES (p_employee_id, p_warehouse_id, p_reason, NOW());

    SET v_export_id = LAST_INSERT_ID();

    -- 2. Duyệt JSON xuất
    DECLARE cur CURSOR FOR
    SELECT variant_id, quantity
    FROM JSON_TABLE(p_items_json, '$[*]'
        COLUMNS (
            variant_id BIGINT PATH '$.variant_id',
            quantity INT PATH '$.quantity'
        )) AS jt;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    OPEN cur;

    read_loop: LOOP
        DECLARE v_needed INT;
        DECLARE v_batch_id BIGINT;
        DECLARE v_batch_qty INT;

        FETCH cur INTO v_variant_id, v_quantity;
        IF v_done THEN LEAVE read_loop; END IF;

        SET v_needed = v_quantity;
        SELECT product_id INTO v_product_id FROM product_variants WHERE id = v_variant_id;

        -- Lấy batch theo FIFO (batch cũ nhất trước)
        DECLARE batch_cur CURSOR FOR
        SELECT id, quantity_available FROM batches
        WHERE variant_id = v_variant_id AND warehouse_id = p_warehouse_id AND quantity_available > 0
        ORDER BY create_date ASC;

        DECLARE batch_done INT DEFAULT FALSE;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET batch_done = TRUE;

        OPEN batch_cur;

        batch_loop: LOOP
            FETCH batch_cur INTO v_batch_id, v_batch_qty;
            IF batch_done THEN LEAVE batch_loop; END IF;

            IF v_needed <= 0 THEN LEAVE batch_loop; END IF;

            IF v_batch_qty >= v_needed THEN
                -- Lấy đủ số lượng
                UPDATE batches
                SET quantity_available = quantity_available - v_needed
                WHERE id = v_batch_id;

                INSERT INTO batches_exported(batch_export_id, export_details_id, quantity)
                VALUES (v_batch_id, v_export_id, v_needed);

                SET v_needed = 0;
            ELSE
                -- Lấy hết batch
                UPDATE batches
                SET quantity_available = 0
                WHERE id = v_batch_id;

                INSERT INTO batches_exported(batch_export_id, export_details_id, quantity)
                VALUES (v_batch_id, v_export_id, v_batch_qty);

                SET v_needed = v_needed - v_batch_qty;
            END IF;
        END LOOP;

        CLOSE batch_cur;

        -- Nếu không đủ tồn kho
        IF v_needed > 0 THEN
            ROLLBACK;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = CONCAT('Not enough stock for variant ', v_variant_id);
        END IF;

    END LOOP;

    CLOSE cur;

    COMMIT;
END$$

DELIMITER ;

DELIMITER $$

CREATE FUNCTION fn_variant_exists(
    p_keyword VARCHAR(255),
    p_variant_filter JSON,
    p_warehouse_id BIGINT
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count
    FROM product_variants pv
    JOIN products p ON pv.product_id = p.id
    JOIN batches b ON b.variant_id = pv.id
    WHERE p.name LIKE CONCAT('%', p_keyword, '%')
      AND b.warehouse_id = p_warehouse_id
      AND b.quantity_available > 0
      AND (p_variant_filter IS NULL OR JSON_CONTAINS(pv.variant_json, p_variant_filter));

    RETURN v_count > 0;
END$$

DELIMITER ;