package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_employees")
public class WarehouseEmployee {
    @Id
    @Column(name = "employee_id")
    private long employeeID;

    @Column(name = "warehouse_id")
    private Long warehouseID;
/*
+--------------+--------+------+-----+---------+-------+
| Field        | Type   | Null | Key | Default | Extra |
+--------------+--------+------+-----+---------+-------+
| employee_id  | bigint | NO   | PRI | NULL    |       |
| warehouse_id | bigint | YES  | MUL | NULL    |       |
+--------------+--------+------+-----+---------+-------+
*/
    public WarehouseEmployee(){}
    public WarehouseEmployee(long eid, Long wid){
        this.employeeID = eid;
        this.warehouseID = wid;
    }
    public long getEmployeeID(){return employeeID;}
    public Long getWarehouseID(){return warehouseID;}
}
