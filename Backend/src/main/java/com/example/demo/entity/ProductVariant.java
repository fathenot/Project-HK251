package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_id", nullable = false)
    private long productID;

    @Column(name = "variant_json")
    private Json variantJson;

/*
+--------------+--------+------+-----+---------+----------------+
| Field        | Type   | Null | Key | Default | Extra          |
+--------------+--------+------+-----+---------+----------------+
| id           | bigint | NO   | PRI | NULL    | auto_increment |
| product_id   | bigint | NO   | MUL | NULL    |                |
| variant_json | json   | YES  |     | NULL    |                |
+--------------+--------+------+-----+---------+----------------+
*/
    public ProductVariant(){}
    public ProductVariant(long id, long productID, String jsonString){
        this.id = id;
        this.productID = productID;
        this.variantJson = new Json(jsonString);
    }
    public long getID(){return id;}
    public long getProductID(){return productID;}
    public Json getVariantJson(){return variantJson;}
}
