package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "manager_id", unique = true)
    private Long managerID; // Long vì mid có thể null
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
    public Store(){}
    public Store(long id, String name, String location, Long mid){
        this.id = id;
        this.name = name;
        this.location = location;
        this.managerID = mid;
    }
    public long getID(){return id;}
    public String getName(){return name;}
    public String getLocation(){return location;}
    public Long getManagerID(){return managerID;}
}
