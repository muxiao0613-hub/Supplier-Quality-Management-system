package com.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.PurchaseRecord;
import com.backend.entity.Supplier;
import com.backend.mapper.PurchaseRecordMapper;
import com.backend.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseRecordService {
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;

    @Autowired
    private SupplierMapper supplierMapper;

    public Page<PurchaseRecord> getRecordList(Long current, Long size, Long supplierId, Integer inspectionResult) {
        Page<PurchaseRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (supplierId != null) {
            wrapper.eq(PurchaseRecord::getSupplierId, supplierId);
        }
        
        if (inspectionResult != null) {
            wrapper.eq(PurchaseRecord::getInspectionResult, inspectionResult);
        }
        
        wrapper.orderByDesc(PurchaseRecord::getArrivalDate);
        return purchaseRecordMapper.selectPage(page, wrapper);
    }

    public PurchaseRecord getRecordById(Long id) {
        return purchaseRecordMapper.selectById(id);
    }

    public boolean createRecord(PurchaseRecord record) {
        Supplier supplier = supplierMapper.selectById(record.getSupplierId());
        if (supplier != null) {
            record.setIsDelayed(record.getExpectedDate() != null && 
                    record.getArrivalDate().isAfter(record.getExpectedDate()) ? 1 : 0);
        }
        return purchaseRecordMapper.insert(record) > 0;
    }

    public boolean updateRecord(PurchaseRecord record) {
        Supplier supplier = supplierMapper.selectById(record.getSupplierId());
        if (supplier != null) {
            record.setIsDelayed(record.getExpectedDate() != null && 
                    record.getArrivalDate().isAfter(record.getExpectedDate()) ? 1 : 0);
        }
        return purchaseRecordMapper.updateById(record) > 0;
    }

    public boolean deleteRecord(Long id) {
        return purchaseRecordMapper.deleteById(id) > 0;
    }
}
