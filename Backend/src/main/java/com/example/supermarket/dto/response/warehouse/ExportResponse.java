package com.example.supermarket.dto.response.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportResponse {
    @JsonProperty("details_id")
    private Long detailsId;

    private String reason;

    @JsonProperty("export_date")
    private LocalDateTime exportDate;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("employee_name")
    private String employeeName;
}
