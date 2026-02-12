package com.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.SupplierScore;
import com.backend.service.SupplierScoreService;
import com.backend.vo.PageResult;
import com.backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/supplier-scores")
@CrossOrigin
public class SupplierScoreController {
    @Autowired
    private SupplierScoreService supplierScoreService;

    @GetMapping
    public Result<PageResult<SupplierScore>> getScoreList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer isRisk) {
        Page<SupplierScore> page = supplierScoreService.getScoreList(current, size, supplierId, isRisk);
        PageResult<SupplierScore> result = new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/risk")
    public Result<List<SupplierScore>> getRiskSuppliers() {
        List<SupplierScore> riskSuppliers = supplierScoreService.getRiskSuppliers();
        return Result.success(riskSuppliers);
    }

    @GetMapping("/risk-count")
    public Result<Long> getRiskCount() {
        Long count = supplierScoreService.getRiskCount();
        return Result.success(count);
    }

    @GetMapping("/{id}")
    public Result<SupplierScore> getScoreById(@PathVariable Long id) {
        SupplierScore score = supplierScoreService.getScoreById(id);
        return Result.success(score);
    }

    @PostMapping("/calculate")
    public Result<SupplierScore> calculateScore(@RequestParam Long supplierId, @RequestParam String scoreDate) {
        try {
            LocalDate date = LocalDate.parse(scoreDate);
            SupplierScore score = supplierScoreService.calculateScore(supplierId, date);
            return Result.success(score);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> createScore(@RequestBody SupplierScore score) {
        boolean success = supplierScoreService.createScore(score);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateScore(@PathVariable Long id, @RequestBody SupplierScore score) {
        score.setId(id);
        boolean success = supplierScoreService.updateScore(score);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteScore(@PathVariable Long id) {
        boolean success = supplierScoreService.deleteScore(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
