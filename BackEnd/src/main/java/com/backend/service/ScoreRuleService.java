package com.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.entity.ScoreRule;
import com.backend.mapper.ScoreRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreRuleService {
    @Autowired
    private ScoreRuleMapper scoreRuleMapper;

    public List<ScoreRule> getAllRules() {
        LambdaQueryWrapper<ScoreRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ScoreRule::getId);
        return scoreRuleMapper.selectList(wrapper);
    }

    public ScoreRule getRuleById(Long id) {
        return scoreRuleMapper.selectById(id);
    }

    public boolean createRule(ScoreRule rule) {
        return scoreRuleMapper.insert(rule) > 0;
    }

    public boolean updateRule(ScoreRule rule) {
        return scoreRuleMapper.updateById(rule) > 0;
    }

    public boolean deleteRule(Long id) {
        return scoreRuleMapper.deleteById(id) > 0;
    }

    public boolean updateRuleWeight(Long id, java.math.BigDecimal weight) {
        ScoreRule rule = new ScoreRule();
        rule.setId(id);
        rule.setWeight(weight);
        return scoreRuleMapper.updateById(rule) > 0;
    }
}
