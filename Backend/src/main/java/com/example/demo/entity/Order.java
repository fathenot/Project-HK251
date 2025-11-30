package com.example.demo.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "customer_id", nullable = false)
    private long customerID;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Pending'")
    private String status; // hay là dùng enum ở đây? -> phải tạo bảng status trong db
    
    @Column(name = "total_money", nullable = false, columnDefinition = "DECIMAL(15,2)")
    private float totalMoney;
    /*
+-------------+---------------+------+-----+-------------------+-------------------+
| Field       | Type          | Null | Key | Default           | Extra             |
+-------------+---------------+------+-----+-------------------+-------------------+
| id          | bigint        | NO   | PRI | NULL              | auto_increment    |
| customer_id | bigint        | NO   | MUL | NULL              |                   |
| created_at  | timestamp     | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| status      | varchar(50)   | NO   |     | Pending           |                   |
| total_money | decimal(15,2) | NO   |     | NULL              |                   |
+-------------+---------------+------+-----+-------------------+-------------------+ 
    */
    public Order(){}
    public Order(int id, int customerID, LocalDateTime createdAt, String status, float totalMoney){
        this.id = id;
        this.customerID = customerID;
        this.createdAt = createdAt;
        this.status = status;
        this.totalMoney = totalMoney;
    }

    public long getID(){return id;}
    public long getCustomerID(){return customerID;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public String getStatus(){return status;}
    public float getTotalMoney(){return totalMoney;}
}
