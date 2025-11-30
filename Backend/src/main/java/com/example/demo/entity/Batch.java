package com.example.demo.entity;

import java.time.LocalDate;

import com.example.demo.entity.utility.DateTimeUtils;

import jakarta.persistence.*;

@Entity
@Table(name = "batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "variant_id", nullable = false)
    private int variantID;
    
    @Column(name = "product_id", nullable = false)
    private int productID;
    
    @Column(name = "warehouse_id", nullable = false)
    private int warehouseID;
    
    @Column(name = "manufacture", length = 255)
    private String manufacturer;
    
    @Column(name = "supplier", length = 255)
    private String supplier;
    
    @Column(name = "quantity_total", nullable = false)
    private int totalQuantity;
    
    @Column(name = "quantity_available", nullable = false)
    private int availableQuantity;
    
    @Column(name = "create_date")
    private LocalDate manufactureDate;
    
    @Column(name = "expiry_date")
    private LocalDate expireDate;
/*
+--------------------+--------------+------+-----+---------+----------------+
| Field              | Type         | Null | Key | Default | Extra          |
+--------------------+--------------+------+-----+---------+----------------+
| id                 | bigint       | NO   | PRI | NULL    | auto_increment |
| variant_id         | bigint       | NO   | MUL | NULL    |                |
| product_id         | bigint       | NO   | MUL | NULL    |                |
| warehouse_id       | bigint       | NO   | MUL | NULL    |                |
| manufacture        | varchar(255) | YES  |     | NULL    |                |
| supplier           | varchar(255) | YES  |     | NULL    |                |
| quantity_total     | int          | NO   |     | NULL    |                |
| quantity_available | int          | NO   |     | NULL    |                |
| create_date        | date         | YES  |     | NULL    |                |
| expiry_date        | date         | YES  |     | NULL    |                |
+--------------------+--------------+------+-----+---------+----------------+
*/
    // constructor
    public Batch(){}
    public Batch(int id, int variantID, int productID, 
        int warehouseID, String manufacturer, String supplier, int totalQuantity, 
        int availableQuantity, String manufactureDate_str, String expiringDate_str){
        this.id = id;
        this.variantID = variantID;
        this.productID = productID;
        this.warehouseID = warehouseID;
        this.manufacturer = manufacturer;
        this.supplier = supplier;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.manufactureDate = DateTimeUtils.createDate(manufactureDate_str);
        this.expireDate = DateTimeUtils.createDate(expiringDate_str);
    }
    // getters
    public int getID() {return this.id;}    
    public int getVariantID() {return this.variantID;}    
    public int getProductID() {return this.productID;}    
    public int getWarehouseID() {return this.warehouseID;}    
    public String getManufacturer() {return this.manufacturer;}    
    public String getSupplier() {return this.supplier;}    
    public int getTotalQuantity() {return this.totalQuantity;}    
    public int getAvailableQuantity() {return this.availableQuantity;}    
    public LocalDate getManufactureDate() {return this.manufactureDate;}
    public LocalDate getExpireDate() {return this.expireDate;}
}