package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_exports_details")
public class WarehouseExportDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    private long detailsID;
    
    @Column(name = "reason", length = 255)
    private String reason;
    
    @Column(name = "employee_export")
    private Long employeeExport;
    
    @CreationTimestamp
    @Column(name = "export_date", nullable = false, updatable = false)
    private LocalDateTime exportDate;
    
    @Column(name = "warehouse_id", nullable = false)
    private long warehouseID;
/*
+-----------------+--------------+------+-----+-------------------+-------------------+
| Field           | Type         | Null | Key | Default           | Extra             |
+-----------------+--------------+------+-----+-------------------+-------------------+
| details_id      | bigint       | NO   | PRI | NULL              | auto_increment    |
| reason          | varchar(255) | YES  |     | NULL              |                   |
| employee_export | bigint       | YES  | MUL | NULL              |                   |
| export_date     | timestamp    | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| warehouse_id    | bigint       | NO   | MUL | NULL              |                   |
+-----------------+--------------+------+-----+-------------------+-------------------+
*/
    public WarehouseExportDetail() {}    
    public WarehouseExportDetail(long detailsID, String reason, Long employeeExport, 
                                 LocalDateTime exportDate, long warehouseID) {
        this.detailsID = detailsID;
        this.reason = reason;
        this.employeeExport = employeeExport;
        this.exportDate = exportDate;
        this.warehouseID = warehouseID;
    }
    public long getDetailsID() {return detailsID;}    
    public String getReason() {return reason;}    
    public Long getEmployeeExport() {return employeeExport;}    
    public LocalDateTime getExportDate() {return exportDate;}
    public long getWarehouseID() {return warehouseID;}
}