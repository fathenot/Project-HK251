package com.example.demo.entity;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.entity.utility.DateTimeUtils;

import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "customer_id", nullable = false, unique = true)
    private long customerID;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
/*
+-------------+-----------+------+-----+-------------------+-------------------+
| Field       | Type      | Null | Key | Default           | Extra             |
+-------------+-----------+------+-----+-------------------+-------------------+
| id          | bigint    | NO   | PRI | NULL              | auto_increment    |
| customer_id | bigint    | NO   | UNI | NULL              |                   |
| created_at  | timestamp | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| updated_at  | timestamp | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
+-------------+-----------+------+-----+-------------------+-------------------+
*/
    public Cart(){}
    public Cart(int id, int customerID, String createDatetime_str, String updateDatetime_str){
        this.id = id;
        this.customerID = customerID;
        this.createdAt = DateTimeUtils.createDatetime(createDatetime_str);
        this.updatedAt = DateTimeUtils.createDatetime(updateDatetime_str);
    }
    public long getID(){return id;}
    public long getCustomerID(){return customerID;}
    public LocalDateTime getCreateTime(){return createdAt;}
    public LocalDateTime getUpdateTime(){return updatedAt;}
}
