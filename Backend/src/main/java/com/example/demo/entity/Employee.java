package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "first_name", length = 100)
    private String firstName;
    
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "hired_at", updatable = false)
    private LocalDate hiredAt;
    
    @Column(name = "manager_id")
    private Long managerID;
/*
+------------+--------------+------+-----+-------------------+-------------------+
| Field      | Type         | Null | Key | Default           | Extra             |
+------------+--------------+------+-----+-------------------+-------------------+
| id         | bigint       | NO   | PRI | NULL              | auto_increment    |
| username   | varchar(255) | NO   | UNI | NULL              |                   |
| password   | varchar(255) | NO   |     | NULL              |                   |
| first_name | varchar(100) | YES  |     | NULL              |                   |
| last_name  | varchar(100) | YES  |     | NULL              |                   |
| hired_at   | timestamp    | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| manager_id | bigint       | YES  | MUL | NULL              |                   |
+------------+--------------+------+-----+-------------------+-------------------+
*/
    // CONSTRUCTOR
    public Employee(){}
    public Employee(long id, String username, String password, String firstName, String lastName, String hiredDate_str, Long managerID){
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hiredAt = DateTimeUtils.createDate(hiredDate_str);
       	this.managerID = managerID;
    }
    // GETTERS
    public long getID(){return id;}
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public LocalDate getHiredDate(){return hiredAt;}
    public Long getManagerID(){return managerID;}
}
