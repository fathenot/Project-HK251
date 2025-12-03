package com.example.supermarket.repository;

import com.example.supermarket.entity.WarehouseEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseEmployeeRepository extends JpaRepository<WarehouseEmployee, Long> {
}
