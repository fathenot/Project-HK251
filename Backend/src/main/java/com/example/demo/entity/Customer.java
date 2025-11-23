package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "first_name", length = 100)
    private String firstName;
    
    @Column(name = "last_name", length = 100)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "loyalty_points", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int loyaltyPt;

    public Customer(){}
    public Customer(int id, String firstName, String lastName, String email, String phone, String address, int loyaltyPt){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.loyaltyPt = loyaltyPt;
    }
    
    public int getId(){return id;}
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getEmail(){return email;}
    public String getPhone(){return phone;}
    public String getAddress(){return address;}
    public int getLoyaltyPt(){return loyaltyPt;}
}