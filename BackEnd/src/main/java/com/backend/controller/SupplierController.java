package com.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.Supplier;
import com.backend.service.SupplierService;
import com.backend.vo.PageResult;
import com.backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public Result<PageResult<Supplier>> getSupplierList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String grade) {
        Page<Supplier> page = supplierService.getSupplierList(current, size, keyword, category, grade);
        PageResult<Supplier> result = new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return Result.success(suppliers);
    }

    @GetMapping("/{id}")
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return Result.success(supplier);
    }

    @PostMapping
    public Result<Void> createSupplier(@RequestBody Supplier supplier) {
        boolean success = supplierService.createSupplier(supplier);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        boolean success = supplierService.updateSupplier(supplier);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSupplier(@PathVariable Long id) {
        boolean success = supplierService.deleteSupplier(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
