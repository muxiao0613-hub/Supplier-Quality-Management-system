package com.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.Supplier;
import com.backend.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    @Autowired
    private SupplierMapper supplierMapper;

    public Page<Supplier> getSupplierList(Long current, Long size, String keyword, String category, String grade) {
        Page<Supplier> page = new Page<>(current, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Supplier::getSupplierName, keyword)
                    .or()
                    .like(Supplier::getSupplierCode, keyword)
                    .or()
                    .like(Supplier::getContactPerson, keyword));
        }
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Supplier::getCategory, category);
        }
        
        if (grade != null && !grade.isEmpty()) {
            wrapper.eq(Supplier::getGrade, grade);
        }
        
        wrapper.orderByDesc(Supplier::getCreateTime);
        return supplierMapper.selectPage(page, wrapper);
    }

    public Supplier getSupplierById(Long id) {
        return supplierMapper.selectById(id);
    }

    public boolean createSupplier(Supplier supplier) {
        return supplierMapper.insert(supplier) > 0;
    }

    public boolean updateSupplier(Supplier supplier) {
        return supplierMapper.updateById(supplier) > 0;
    }

    public boolean deleteSupplier(Long id) {
        return supplierMapper.deleteById(id) > 0;
    }

    public java.util.List<Supplier> getAllSuppliers() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1);
        wrapper.orderByDesc(Supplier::getCreateTime);
        return supplierMapper.selectList(wrapper);
    }
}
