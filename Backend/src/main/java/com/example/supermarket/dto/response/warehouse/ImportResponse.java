package com.example.supermarket.dto.response.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResponse {
    @JsonProperty("details_id")
    private Long detailsId;

    private String supplier;

    @JsonProperty("import_date")
    private LocalDateTime importDate;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("employee_name")
    private String employeeName;
}
