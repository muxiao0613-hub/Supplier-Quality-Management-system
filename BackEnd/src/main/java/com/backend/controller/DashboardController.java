package com.backend.controller;

import com.backend.entity.SupplierScore;
import com.backend.entity.PurchaseRecord;
import com.backend.entity.Supplier;
import com.backend.service.SupplierScoreService;
import com.backend.service.PurchaseRecordService;
import com.backend.service.SupplierService;
import com.backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    @Autowired
    private SupplierScoreService supplierScoreService;

    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long riskCount = supplierScoreService.getRiskCount();
        stats.put("riskCount", riskCount);
        
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        stats.put("totalSuppliers", suppliers.size());
        
        List<PurchaseRecord> allRecords = purchaseRecordService.getRecordList(1L, 10000L, null, null).getRecords();
        stats.put("totalRecords", allRecords.size());
        
        long qualifiedCount = allRecords.stream().filter(r -> r.getInspectionResult() == 1).count();
        stats.put("qualifiedCount", qualifiedCount);
        
        return Result.success(stats);
    }

    @GetMapping("/score-ranking")
    public Result<List<Map<String, Object>>> getScoreRanking() {
        List<SupplierScore> scores = supplierScoreService.getScoreList(1L, 100L, null, null).getRecords();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        
        Map<Long, String> supplierNames = new HashMap<>();
        for (Supplier supplier : suppliers) {
            supplierNames.put(supplier.getId(), supplier.getSupplierName());
        }
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (SupplierScore score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("supplierName", supplierNames.get(score.getSupplierId()));
            item.put("totalScore", score.getTotalScore());
            item.put("qualityRate", score.getQualityRate());
            item.put("ontimeRate", score.getOntimeRate());
            ranking.add(item);
        }
        
        ranking.sort((a, b) -> ((BigDecimal) b.get("totalScore")).compareTo((BigDecimal) a.get("totalScore")));
        
        return Result.success(ranking);
    }

    @GetMapping("/quality-trend")
    public Result<List<Map<String, Object>>> getQualityTrend() {
        List<PurchaseRecord> records = purchaseRecordService.getRecordList(1L, 10000L, null, null).getRecords();
        
        Map<String, Map<String, Object>> dateMap = new LinkedHashMap<>();
        
        for (PurchaseRecord record : records) {
            String dateKey = record.getArrivalDate().toString().substring(0, 7);
            
            if (!dateMap.containsKey(dateKey)) {
                Map<String, Object> dateData = new HashMap<>();
                dateData.put("date", dateKey);
                dateData.put("total", 0);
                dateData.put("qualified", 0);
                dateMap.put(dateKey, dateData);
            }
            
            Map<String, Object> dateData = dateMap.get(dateKey);
            dateData.put("total", (Integer) dateData.get("total") + 1);
            if (record.getInspectionResult() == 1) {
                dateData.put("qualified", (Integer) dateData.get("qualified") + 1);
            }
        }
        
        List<Map<String, Object>> trend = new ArrayList<>(dateMap.values());
        return Result.success(trend);
    }

    @GetMapping("/supplier-comparison")
    public Result<List<Map<String, Object>>> getSupplierComparison() {
        List<SupplierScore> scores = supplierScoreService.getScoreList(1L, 100L, null, null).getRecords();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        
        Map<Long, String> supplierNames = new HashMap<>();
        for (Supplier supplier : suppliers) {
            supplierNames.put(supplier.getId(), supplier.getSupplierName());
        }
        
        List<Map<String, Object>> comparison = new ArrayList<>();
        for (SupplierScore score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("supplierName", supplierNames.get(score.getSupplierId()));
            item.put("qualityRate", score.getQualityRate());
            item.put("ontimeRate", score.getOntimeRate());
            item.put("serviceScore", score.getServiceScore());
            item.put("totalScore", score.getTotalScore());
            comparison.add(item);
        }
        
        return Result.success(comparison);
    }
}
