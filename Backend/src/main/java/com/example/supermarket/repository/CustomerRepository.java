package com.example.supermarket.repository;

import com.example.supermarket.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Boolean existsByEmail(String email);
    Optional<Customer> findByEmail(String email);
}
