package com.renergetic.ruleevaluationservice.scheduler;

import com.renergetic.ruleevaluationservice.model.AssetRule;
import com.renergetic.ruleevaluationservice.repository.AssetRuleRepository;
import com.renergetic.ruleevaluationservice.service.RuleEvaluationService;
import com.renergetic.ruleevaluationservice.utils.AssetRuleUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RuleExecutionScheduler {

    @Autowired
    RuleEvaluationService ruleEvaluationService;

    private HashMap<String, RuleEvaluationResult> evaluationResultHashMap = new HashMap<>();

    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "${rule.executionCRON}")
    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public void executeRules(){
        ruleEvaluationService.retrieveAndExecuteAllRules();
    }

    @Getter
    @Setter
    public class RuleEvaluationResult {
        private String result;
        private LocalDateTime execution;
    }
}
