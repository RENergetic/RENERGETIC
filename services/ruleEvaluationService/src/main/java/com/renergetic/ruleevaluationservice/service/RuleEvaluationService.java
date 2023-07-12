package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.model.NotificationDefinition;
import com.renergetic.common.model.NotificationSchedule;
import com.renergetic.common.model.NotificationType;
import com.renergetic.common.repository.NotificationDefinitionRepository;
import com.renergetic.common.repository.NotificationScheduleRepository;
import com.renergetic.ruleevaluationservice.dao.DataResponse;
import com.renergetic.ruleevaluationservice.dao.EvaluationResult;
import com.renergetic.ruleevaluationservice.exception.RuleEvaluationException;
import com.renergetic.ruleevaluationservice.model.AssetRule;
import com.renergetic.ruleevaluationservice.repository.AssetRuleRepository;
import com.renergetic.ruleevaluationservice.utils.AssetRuleUtils;
import com.renergetic.ruleevaluationservice.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RuleEvaluationService {
    @Value("${rule.executionCRON}")
    private String executionCRON;

    @Autowired
    private AssetRuleRepository assetRuleRepository;

    @Autowired
    private NotificationDefinitionRepository notificationDefinitionRepository;

    @Autowired
    private NotificationScheduleRepository notificationScheduleRepository;

    @Autowired
    DataService dataService;

    public List<EvaluationResult> retrieveAndExecuteAllRules(){
        List<AssetRule> assetRules = assetRuleRepository.findByActiveTrue();
        return executeAllRules(assetRules);
    }

    public List<EvaluationResult> retrieveAndExecuteAllRulesForAssetId(Long id){
        List<AssetRule> assetRules = assetRuleRepository.findByAssetId(id);
        return executeAllRules(assetRules);
    }

    public List<EvaluationResult> executeAllRules(List<AssetRule> assetRules){
        List<EvaluationResult> evaluationResults = new ArrayList<>();
        Set<Thread> threads = new HashSet<>();
        for(AssetRule assetRule : assetRules){
            Thread thread = new Thread(() -> {
                evaluationResults.add(executeRule(assetRule));
            });
            thread.start();
            threads.add(thread);
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return evaluationResults;
    }

    public EvaluationResult executeRule(Long id){
        Optional<AssetRule> assetRule = assetRuleRepository.findById(id);
        return assetRule.map(this::executeRule).orElse(null);
    }

    public EvaluationResult executeRule(AssetRule assetRule){
        EvaluationResult evaluationResult = new EvaluationResult();
        if(!assetRule.isActive()){
            evaluationResult.setExecutedString("inactive");
            return evaluationResult;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        try{
            evaluationResult.setExecutedReadableString(AssetRuleUtils.transformRuleToReadableName(assetRule));
            setEvaluationStringAndData(assetRule, evaluationResult);
            evaluationResult.setExecutionResult(evaluateString(evaluationResult.getExecutedString()));
        } catch (RuleEvaluationException ree){
            evaluationResult.setExecutionResult(null);
            evaluationResult.setErrorMessage(ree.getMessage());
        } catch (ScriptException se) {
            evaluationResult.setExecutionResult(null);
            evaluationResult.setErrorMessage("evaluation failed.");
        }

        if(evaluationResult.getErrorMessage() != null && evaluationResult.getExecutionResult().equals("true")){
            Optional<NotificationDefinition> ndo = notificationDefinitionRepository.findByCode(evaluationResult.getExecutedReadableString());
            NotificationDefinition nd;
            if(ndo.isEmpty()){
                nd = new NotificationDefinition();
                nd.setCode(evaluationResult.getExecutedReadableString());
                nd.setMessage(evaluationResult.getExecutedReadableString());
            }
            else
                nd = ndo.get();
            nd.setType(evaluationResult.getErrorMessage() == null ? NotificationType.anomaly : NotificationType.error);
            notificationDefinitionRepository.save(nd);

            CronExpression cronTrigger = CronExpression.parse(executionCRON);
            LocalDateTime nextExecution = cronTrigger.next(currentTime);

            NotificationSchedule notificationSchedule = new NotificationSchedule();
            notificationSchedule.setAsset(assetRule.getAsset());
            notificationSchedule.setDateFrom(currentTime);
            notificationSchedule.setDateTo(nextExecution);
            notificationSchedule.setDefinition(nd);
            notificationSchedule.setMeasurement(assetRule.getMeasurement1());
            notificationSchedule.setNotificationTimestamp(LocalDateTime.now());
            notificationSchedule = notificationScheduleRepository.save(notificationSchedule);
            evaluationResult.setNotificationSchedule(notificationSchedule);
        }

        return evaluationResult;
    }

    private String evaluateString(String expression) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        return engine.eval(expression).toString();
    }

    private void setEvaluationStringAndData(AssetRule assetRule, EvaluationResult evaluationResult) throws RuleEvaluationException {
        DataResponse dataMS1 = dataService.getData(assetRule.getMeasurement1(), assetRule.getFunctionMeasurement1(),
                assetRule.getTimeRangeMeasurement1(), TimeUtils.offsetCurrentInstantOfAtLeast3Hours(assetRule.getTimeRangeMeasurement1()).toEpochMilli(),
                Optional.empty());

        if(dataMS1 == null || dataMS1.getDataUsedForComparison() == null || dataMS1.getRawResult() == null || dataMS1.getRawResult().size() == 0)
            throw new RuleEvaluationException("Measurement 1 data could not be retrieved.");

        evaluationResult.setDataResponseMeasurement1(dataMS1);

        String expression;
        if(assetRule.getMeasurement2() != null){
            DataResponse dataMS2 = dataService.getData(assetRule.getMeasurement2(), assetRule.getFunctionMeasurement2(),
                    assetRule.getTimeRangeMeasurement2(), TimeUtils.offsetCurrentInstantOfAtLeast3Hours(assetRule.getTimeRangeMeasurement2()).toEpochMilli(),
                    Optional.empty());

            if(dataMS2 == null || dataMS2.getDataUsedForComparison() == null || dataMS2.getRawResult() == null || dataMS2.getRawResult().size() == 0)
                throw new RuleEvaluationException("Measurement 2 data could not be retrieved.");

            evaluationResult.setDataResponseMeasurement2(dataMS2);
            evaluationResult.setExecutedString(dataMS1.getDataUsedForComparison().getValue()+assetRule.getComparator()+dataMS2.getDataUsedForComparison().getValue());
        } else {
            String threshold = assetRule.isCompareToConfigThreshold() ?
                    assetRule.getAsset().getDetails().stream().filter(ad -> ad.getKey().equals("rule_threshold")).findFirst().orElseThrow().getValue()
                    :
                    assetRule.getManualThreshold();

            if(threshold == null)
                throw new RuleEvaluationException("Asset rule configuration missing a threshold value.");

            evaluationResult.setDataResponseMeasurement2(null);
            evaluationResult.setExecutedString(dataMS1.getDataUsedForComparison().getValue()+assetRule.getComparator()+threshold);
        }

    }
}
