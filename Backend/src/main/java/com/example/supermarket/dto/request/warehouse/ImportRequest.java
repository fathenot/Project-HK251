package com.example.supermarket.dto.request.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportRequest {
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private String supplier;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @NotNull(message = "Employee ID is required")
    private Long employeeImport;

    private List<Long> batchIds;
}
