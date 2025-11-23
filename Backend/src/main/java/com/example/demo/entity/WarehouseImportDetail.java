package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_imports_details")
public class WarehouseImportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    private long detailsID;
    
    @Column(name = "supplier", length = 255)
    private String supplier;
    
    @CreationTimestamp
    @Column(name = "import_date", nullable = false, updatable = false)
    private LocalDateTime importDate;
    
    @Column(name = "unit_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private float unitPrice;
    
    @Column(name = "employee_import")
    private Long employeeImport;
    
    @Column(name = "warehouse_id", nullable = false)
    private long warehouseID;
/*
+-----------------+---------------+------+-----+-------------------+-------------------+
| Field           | Type          | Null | Key | Default           | Extra             |
+-----------------+---------------+------+-----+-------------------+-------------------+
| details_id      | bigint        | NO   | PRI | NULL              | auto_increment    |
| supplier        | varchar(255)  | YES  |     | NULL              |                   |
| import_date     | timestamp     | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| unit_price      | decimal(10,2) | NO   |     | NULL              |                   |
| employee_import | bigint        | YES  | MUL | NULL              |                   |
| warehouse_id    | bigint        | NO   | MUL | NULL              |                   |
+-----------------+---------------+------+-----+-------------------+-------------------+
*/
    public WarehouseImportDetail(){}
    
    public WarehouseImportDetail(long detailsID, String supplier, LocalDateTime importDate, 
                                 float unitPrice, Long employeeImport, long warehouseID){
        this.detailsID = detailsID;
        this.supplier = supplier;
        this.importDate = importDate;
        this.unitPrice = unitPrice;
        this.employeeImport = employeeImport;
        this.warehouseID = warehouseID;
    }
    public long getDetailsID(){return detailsID;}
    public String getSupplier(){return supplier;}
    public LocalDateTime getImportDate(){return importDate;}
    public float getUnitPrice(){return unitPrice;}
    public Long getEmployeeImport(){return employeeImport;}
    public long getWarehouseID(){return warehouseID;}
}
