package com.example.demo.entity;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    public Cart(int id, int cid, String create, String update){
        this.id = id;
        this.customerID = cid;
        this.createdAt = DateTimeUtils.createDatetime(create);
        this.updatedAt = DateTimeUtils.createDatetime(update);
    }
    public long getID(){return id;}
    public long getCustomerID(){return customerID;}
    public LocalDateTime getCreatedTime(){return createdAt;}
    public LocalDateTime getUpdatedTime(){return updatedAt;}
}
