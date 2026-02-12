package com.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("supplier_score")
public class SupplierScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long supplierId;
    private BigDecimal qualityRate;
    private BigDecimal ontimeRate;
    private BigDecimal serviceScore;
    private BigDecimal totalScore;
    private Integer isRisk;
    private LocalDate scoreDate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
