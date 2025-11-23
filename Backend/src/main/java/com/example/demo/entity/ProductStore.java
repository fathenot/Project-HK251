package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_stores")
@IdClass(ProductStoreID.class)
public class ProductStore {
    @Id
    @Column(name = "product_id")
    private long productID;

    @Id
    @Column(name = "store_id")
    private long storeID;

    @Column(name = "quantity_in_stock", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int quantity;
/*
+-------------------+--------+------+-----+---------+-------+
| Field             | Type   | Null | Key | Default | Extra |
+-------------------+--------+------+-----+---------+-------+
| product_id        | bigint | NO   | PRI | NULL    |       |
| store_id          | bigint | NO   | PRI | NULL    |       |
| quantity_in_stock | int    | NO   |     | 0       |       |
+-------------------+--------+------+-----+---------+-------+
*/
    public ProductStore(){}
    public ProductStore(long pid, long sid, int q){
        this.productID = pid;
        this.storeID = sid;
        this.quantity = q;
    }
    public long getProductID(){return productID;}
    public long getStoreID(){return storeID;}
    public int getQuantity(){return quantity;}
}
