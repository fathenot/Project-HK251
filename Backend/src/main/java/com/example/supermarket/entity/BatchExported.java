package com.example.supermarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "batches_exported")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExported {

    @Id
    @Column(name = "batch_export_id")
    private Long batchExportId;

    @Column(name = "export_details_id")
    private Long exportDetailsId;

    private Integer quantity;
}

