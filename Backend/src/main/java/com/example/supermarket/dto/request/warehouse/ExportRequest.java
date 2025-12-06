package com.example.supermarket.dto.request.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private String reason;

    @NotNull(message = "Employee ID is required")
    private Long employeeExport;

    private List<Long> batchIds;
}
