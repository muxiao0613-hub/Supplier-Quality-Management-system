package com.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.PurchaseRecord;
import com.backend.service.PurchaseRecordService;
import com.backend.vo.PageResult;
import com.backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-records")
@CrossOrigin
public class PurchaseRecordController {
    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @GetMapping
    public Result<PageResult<PurchaseRecord>> getRecordList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer inspectionResult) {
        Page<PurchaseRecord> page = purchaseRecordService.getRecordList(current, size, supplierId, inspectionResult);
        PageResult<PurchaseRecord> result = new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PurchaseRecord> getRecordById(@PathVariable Long id) {
        PurchaseRecord record = purchaseRecordService.getRecordById(id);
        return Result.success(record);
    }

    @PostMapping
    public Result<Void> createRecord(@RequestBody PurchaseRecord record) {
        boolean success = purchaseRecordService.createRecord(record);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @RequestBody PurchaseRecord record) {
        record.setId(id);
        boolean success = purchaseRecordService.updateRecord(record);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        boolean success = purchaseRecordService.deleteRecord(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
