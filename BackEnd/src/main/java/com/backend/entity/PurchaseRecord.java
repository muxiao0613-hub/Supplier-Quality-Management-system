package com.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_record")
public class PurchaseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long supplierId;
    private String productName;
    private Integer quantity;
    private String unit;
    private Integer inspectionResult;
    private String unqualifiedReason;
    private LocalDate arrivalDate;
    private LocalDate expectedDate;
    private Integer isDelayed;
    private Integer paymentStatus;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
