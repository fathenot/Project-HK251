package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_employees")
public class SaleEmployee {
    @Id
    @Column(name = "employee_id")
    private long employeeID;

    @Column(name = "store_id")
    private Long storeID;

    @Column(name = "total_sales", columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private float totalSales; // không nên cho trường này nullable!
/*
+-------------+---------------+------+-----+---------+-------+
| Field       | Type          | Null | Key | Default | Extra |
+-------------+---------------+------+-----+---------+-------+
| employee_id | bigint        | NO   | PRI | NULL    |       |
| store_id    | bigint        | YES  | MUL | NULL    |       |
| total_sales | decimal(15,2) | YES  |     | 0.00    |       |
+-------------+---------------+------+-----+---------+-------+
*/
    public SaleEmployee(){}
    public SaleEmployee(long eid, long sid, float totalSales){
        this.employeeID = eid;
        this.storeID = sid;
        this.totalSales = totalSales;
    }
    public long getEmployeeID(){return employeeID;}
    public Long getStoreID(){return storeID;}
    public float getTotalSales(){return totalSales;}
}
