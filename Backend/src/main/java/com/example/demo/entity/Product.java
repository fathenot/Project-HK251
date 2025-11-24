package com.example.demo.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "batches")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "SKU")
    private String SKU; 
    /* What is this?
    - SKU stands for stock-keeping unit, which is a unique alphanumeric code a business assigns to each product it sells to keep track of its inventory
    - Every variation of a product, such as different sizes or colors, will have its own unique SKU.
    */
   @Column(name = "barcode")
    private String barcode;
    /*
+-------------+---------------+------+-----+---------+----------------+
| Field       | Type          | Null | Key | Default | Extra          |
+-------------+---------------+------+-----+---------+----------------+
| id          | bigint        | NO   | PRI | NULL    | auto_increment |
| name        | varchar(255)  | NO   |     | NULL    |                |
| description | text          | YES  |     | NULL    |                |
| price       | decimal(10,2) | NO   |     | NULL    |                |
| SKU         | varchar(100)  | YES  | UNI | NULL    |                |
| barcode     | varchar(255)  | YES  | UNI | NULL    |                |
+-------------+---------------+------+-----+---------+----------------+
    */
    public Product(int id, String name, String description, float price, String SKU, String barcode){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.SKU = SKU;
        this.barcode = barcode;
    }

    public int getID(){return id;}
    public String getName(){return name;}
    public String getDescription(){return description;}
    public float getPrice(){return price;}
    public String getSKU(){return SKU;}
    public String getBarcode(){return barcode;}
}
