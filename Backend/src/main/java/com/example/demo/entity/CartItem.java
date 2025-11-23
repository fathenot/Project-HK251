package com.example.demo.entity;
import jakarta.persistence.*;


@Entity
@Table(name = "cart_items")
@IdClass(CartItemID.class)
public class CartItem {
    @Id
    @Column(name = "cart_id")
    private long cartID;
    @Id
    @Column(name = "product_id")
    private long productID;
    @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
    private int quantity;
    @Column(name = "sub_total", nullable = false, precision = 12, scale = 2)
    private float subtotal; // what is this?
/*
+------------+---------------+------+-----+---------+-------+
| Field      | Type          | Null | Key | Default | Extra |
+------------+---------------+------+-----+---------+-------+
| cart_id    | bigint        | NO   | PRI | NULL    |       |
| product_id | bigint        | NO   | PRI | NULL    |       |
| quantity   | int           | NO   |     | 1       |       |
| sub_total  | decimal(12,2) | NO   |     | NULL    |       |
+------------+---------------+------+-----+---------+-------+
*/
    public CartItem(){}
    public CartItem(int cartid, int productid, int quantity, float subtotal){
        this.cartID = cartid;
        this.productID = productid;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public long CartID(){return cartID;}
    public long getProductID(){return productID;}
    public int getQuantity(){return quantity;}
    public float getSubtotal(){return subtotal;}
}
