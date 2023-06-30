package com.renergetic.ruleevaluationservice.scheduler;

import com.renergetic.ruleevaluationservice.model.AssetRule;
import com.renergetic.ruleevaluationservice.repository.AssetRuleRepository;
import com.renergetic.ruleevaluationservice.utils.AssetRuleUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RuleExecutionScheduler {
    @Autowired
    private AssetRuleRepository assetRuleRepository;

    private HashMap<String, RuleEvaluationResult> evaluationResultHashMap = new HashMap<>();

    @Scheduled(cron = "0 * * * * *")
    public void executeRules(){
        List<AssetRule> assetRules = assetRuleRepository.findByActiveTrue();
        Set<Thread> threads = new HashSet<>();
        for(AssetRule assetRule : assetRules){
            String name = AssetRuleUtils.transformRuleToReadableName(assetRule);
            boolean needUpdate = true;
            if(evaluationResultHashMap.containsKey(name)){
                RuleEvaluationResult ruleEvaluationResult = evaluationResultHashMap.get(name);
                //TODO: If individual element need evaluation, compare here if needs update effectively.
            }

            if(needUpdate){
                Thread thread = new Thread(() -> {

                });
                thread.start();
                threads.add(thread);
            }
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Getter
    @Setter
    public class RuleEvaluationResult {
        private String result;
        private LocalDateTime execution;
    }
}
