package com.backend.controller;

import com.backend.entity.ScoreRule;
import com.backend.service.ScoreRuleService;
import com.backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/score-rules")
@CrossOrigin
public class ScoreRuleController {
    @Autowired
    private ScoreRuleService scoreRuleService;

    @GetMapping
    public Result<List<ScoreRule>> getAllRules() {
        List<ScoreRule> rules = scoreRuleService.getAllRules();
        return Result.success(rules);
    }

    @GetMapping("/{id}")
    public Result<ScoreRule> getRuleById(@PathVariable Long id) {
        ScoreRule rule = scoreRuleService.getRuleById(id);
        return Result.success(rule);
    }

    @PostMapping
    public Result<Void> createRule(@RequestBody ScoreRule rule) {
        boolean success = scoreRuleService.createRule(rule);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<Void> updateRule(@PathVariable Long id, @RequestBody ScoreRule rule) {
        rule.setId(id);
        boolean success = scoreRuleService.updateRule(rule);
        return success ? Result.success() : Result.error("更新失败");
    }

    @PutMapping("/{id}/weight")
    public Result<Void> updateRuleWeight(@PathVariable Long id, @RequestParam BigDecimal weight) {
        boolean success = scoreRuleService.updateRuleWeight(id, weight);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        boolean success = scoreRuleService.deleteRule(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
