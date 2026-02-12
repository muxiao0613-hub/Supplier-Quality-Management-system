package com.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.entity.SupplierScore;
import com.backend.entity.ScoreRule;
import com.backend.entity.PurchaseRecord;
import com.backend.mapper.SupplierScoreMapper;
import com.backend.mapper.ScoreRuleMapper;
import com.backend.mapper.PurchaseRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SupplierScoreService {
    @Autowired
    private SupplierScoreMapper supplierScoreMapper;

    @Autowired
    private ScoreRuleMapper scoreRuleMapper;

    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;

    public Page<SupplierScore> getScoreList(Long current, Long size, Long supplierId, Integer isRisk) {
        Page<SupplierScore> page = new Page<>(current, size);
        LambdaQueryWrapper<SupplierScore> wrapper = new LambdaQueryWrapper<>();
        
        if (supplierId != null) {
            wrapper.eq(SupplierScore::getSupplierId, supplierId);
        }
        
        if (isRisk != null) {
            wrapper.eq(SupplierScore::getIsRisk, isRisk);
        }
        
        wrapper.orderByDesc(SupplierScore::getScoreDate);
        return supplierScoreMapper.selectPage(page, wrapper);
    }

    public SupplierScore getScoreById(Long id) {
        return supplierScoreMapper.selectById(id);
    }

    public SupplierScore calculateScore(Long supplierId, LocalDate scoreDate) {
        List<ScoreRule> rules = scoreRuleMapper.selectList(
                new LambdaQueryWrapper<ScoreRule>()
                        .eq(ScoreRule::getStatus, 1)
        );

        if (rules.size() != 3) {
            throw new RuntimeException("评分规则配置不正确");
        }

        BigDecimal qualityWeight = BigDecimal.ZERO;
        BigDecimal ontimeWeight = BigDecimal.ZERO;
        BigDecimal serviceWeight = BigDecimal.ZERO;

        for (ScoreRule rule : rules) {
            if ("QUALITY_RATE".equals(rule.getRuleCode())) {
                qualityWeight = rule.getWeight();
            } else if ("ONTIME_RATE".equals(rule.getRuleCode())) {
                ontimeWeight = rule.getWeight();
            } else if ("SERVICE_SCORE".equals(rule.getRuleCode())) {
                serviceWeight = rule.getWeight();
            }
        }

        LambdaQueryWrapper<PurchaseRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(PurchaseRecord::getSupplierId, supplierId);
        List<PurchaseRecord> records = purchaseRecordMapper.selectList(recordWrapper);

        BigDecimal qualityRate = calculateQualityRate(records);
        BigDecimal ontimeRate = calculateOntimeRate(records);
        BigDecimal serviceScore = new BigDecimal("80.00");

        BigDecimal totalScore = qualityRate.multiply(qualityWeight)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .add(ontimeRate.multiply(ontimeWeight).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                .add(serviceScore.multiply(serviceWeight).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

        SupplierScore score = new SupplierScore();
        score.setSupplierId(supplierId);
        score.setQualityRate(qualityRate);
        score.setOntimeRate(ontimeRate);
        score.setServiceScore(serviceScore);
        score.setTotalScore(totalScore);
        score.setIsRisk(totalScore.compareTo(new BigDecimal("60")) < 0 ? 1 : 0);
        score.setScoreDate(scoreDate);

        return score;
    }

    private BigDecimal calculateQualityRate(List<PurchaseRecord> records) {
        if (records == null || records.isEmpty()) {
            return new BigDecimal("100.00");
        }

        long total = records.size();
        long qualified = records.stream()
                .filter(r -> r.getInspectionResult() == 1)
                .count();

        return new BigDecimal(qualified)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOntimeRate(List<PurchaseRecord> records) {
        if (records == null || records.isEmpty()) {
            return new BigDecimal("100.00");
        }

        long total = records.size();
        long ontime = records.stream()
                .filter(r -> r.getIsDelayed() == 0)
                .count();

        return new BigDecimal(ontime)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
    }

    public boolean createScore(SupplierScore score) {
        return supplierScoreMapper.insert(score) > 0;
    }

    public boolean updateScore(SupplierScore score) {
        return supplierScoreMapper.updateById(score) > 0;
    }

    public boolean deleteScore(Long id) {
        return supplierScoreMapper.deleteById(id) > 0;
    }

    public List<SupplierScore> getRiskSuppliers() {
        LambdaQueryWrapper<SupplierScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierScore::getIsRisk, 1);
        wrapper.orderByDesc(SupplierScore::getScoreDate);
        wrapper.last("LIMIT 100");
        return supplierScoreMapper.selectList(wrapper);
    }

    public Long getRiskCount() {
        LambdaQueryWrapper<SupplierScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierScore::getIsRisk, 1);
        return supplierScoreMapper.selectCount(wrapper);
    }
}
