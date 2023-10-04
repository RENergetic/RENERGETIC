package com.renergetic.ruleevaluationservice.scheduler;

import com.renergetic.ruleevaluationservice.service.RuleEvaluationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class RuleExecutionScheduler {

    @Autowired
    RuleEvaluationService ruleEvaluationService;

    private HashMap<String, RuleEvaluationResult> evaluationResultHashMap = new HashMap<>();

    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "${rule.executionCRON}")
    @Transactional(propagation= Propagation.REQUIRED)
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
