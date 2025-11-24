package com.example.demo.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "location", columnDefinition = "TEXT")
    private String location;
    
    @Column(name = "manager_id", unique = true)
    private Long managerID;
/*
+------------+--------------+------+-----+---------+----------------+
| Field      | Type         | Null | Key | Default | Extra          |
+------------+--------------+------+-----+---------+----------------+
| id         | bigint       | NO   | PRI | NULL    | auto_increment |
| name       | varchar(255) | NO   |     | NULL    |                |
| location   | text         | YES  |     | NULL    |                |
| manager_id | bigint       | YES  | UNI | NULL    |                |
+------------+--------------+------+-----+---------+----------------+
*/
    public Warehouse(){}    
    public Warehouse(long id, String name, String location, Long managerID) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.managerID = managerID;
    }    
    public long getID(){return id;}
    public String getName(){return name;}
    public String getLocation(){return location;}
    public Long getManagerID(){return managerID;}
}
