package com.example.supermarket.repository;

import com.example.supermarket.entity.SalesEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesEmployeeRepository extends JpaRepository<SalesEmployee, Long> {
}
