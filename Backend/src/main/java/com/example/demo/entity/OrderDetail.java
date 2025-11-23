package com.example.demo.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
@IdClass(OrderDetailID.class)
public class OrderDetail {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderID;

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
| order_id   | bigint        | NO   | PRI | NULL    |       |
| product_id | bigint        | NO   | PRI | NULL    |       |
| quantity   | int           | NO   |     | NULL    |       |
| sub_total  | decimal(12,2) | NO   |     | NULL    |       |
+------------+---------------+------+-----+---------+-------+
    */
    public OrderDetail(){}
    public OrderDetail(int orderID, int productID, int quantity, float subtotal){
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public long getOrderID(){return orderID;}
    public long getProductID(){return productID;}
    public int getQuantity(){return quantity;}
    public float getSubtotal(){return subtotal;}
}
