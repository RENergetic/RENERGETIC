package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.model.*;
import com.renergetic.common.repository.*;
import com.renergetic.ruleevaluationservice.dao.EvaluationResult;
import com.renergetic.ruleevaluationservice.dao.MeasurementSimplifiedDAO;
import com.renergetic.ruleevaluationservice.exception.RuleEvaluationException;
import com.renergetic.ruleevaluationservice.utils.RuleUtils;
import com.renergetic.ruleevaluationservice.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class RuleEvaluationService {
    @Value("${rule.executionCRON}")
    private String executionCRON;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private NotificationDefinitionRepository notificationDefinitionRepository;

    @Autowired
    private NotificationScheduleRepository notificationScheduleRepository;

    @Autowired
    private DemandDefinitionRepository demandDefinitionRepository;

    @Autowired
    private DemandScheduleRepository demandScheduleRepository;

    @Autowired
    DataService dataService;

    public List<List<EvaluationResult>> retrieveAndExecuteAllRules(){
        List<Rule> rules = ruleRepository.findByActiveTrueAndRootTrue();
        return executeAllRules(rules);
    }

    public List<List<EvaluationResult>> executeAllRules(List<Rule> rules){
        List<List<EvaluationResult>> evaluationResults = new ArrayList<>();
        Set<Thread> threads = new HashSet<>();
        for(Rule rule : rules){
            Thread thread = new Thread(() -> {
                try {
                    evaluationResults.add(executeRule(rule));
                } catch (Exception e) {
                    e.printStackTrace();
                    EvaluationResult evaluationResult = new EvaluationResult();
                    evaluationResult.setErrorMessage(e.getMessage());
                    List<EvaluationResult> list = new ArrayList<>();
                    list.add(evaluationResult);
                    evaluationResults.add(list);
                }
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

    public List<EvaluationResult> executeRule(Long id){
        Optional<Rule> assetRule = ruleRepository.findById(id);
        return assetRule.map(this::executeRule).orElse(null);
    }

    public List<EvaluationResult> executeRule(Rule rule){
        List<EvaluationResult> evaluationResults = new ArrayList<>();
        executeRule(rule, evaluationResults);
        return evaluationResults;
    }

    public void executeRule(Rule rule, List<EvaluationResult> stepsResults){
        EvaluationResult evaluationResult = new EvaluationResult();
        stepsResults.add(evaluationResult);
        if(rule == null){
            evaluationResult.setExecutedString("reached end of rule chain without action.");
        }
        else if(rule.getActive() != null && !rule.getActive()){
            evaluationResult.setExecutedString("inactive");
        }
        else if (rule.getRuleDefinition() != null) {
            RuleDefinition ruleDefinition = rule.getRuleDefinition();
            try{
                evaluationResult.setExecutedReadableString(RuleUtils.transformRuleToReadableName(ruleDefinition));
                setEvaluationStringAndData(ruleDefinition, evaluationResult);
                evaluationResult.setExecutionResult(evaluateString(evaluationResult.getExecutedString()));
            } catch (RuleEvaluationException ree){
                evaluationResult.setExecutionResult(null);
                evaluationResult.setErrorMessage(ree.getMessage());
            } catch (ScriptException se) {
                evaluationResult.setExecutionResult(null);
                evaluationResult.setErrorMessage("evaluation failed.");
            }

            if(evaluationResult.getExecutionResult() != null && evaluationResult.getExecutionResult().equals("true")){
                //evaluationResult.setNotificationSchedule(createNotification(ruleDefinition, evaluationResult));
                executeRule(rule.getPositiveRule());
            } else if (evaluationResult.getExecutionResult() != null && evaluationResult.getExecutionResult().equals("false")) {
                executeRule(rule.getNegativeRule());
            }
        } else if (rule.getRuleAction() != null) {
            //CronExpression cronTrigger = CronExpression.parse(executionCRON);
            LocalDateTime currentTime = LocalDateTime.now();
            RuleAction ruleAction = rule.getRuleAction();
            //LocalDateTime nextExecution = cronTrigger.next(currentTime);

            DemandSchedule demandSchedule = demandScheduleRepository
                    .findByAssetIdAndDemandDefinitionIdAndDemandStartLessThanEqualAndDemandStopGreaterThanEqual(
                            ruleAction.getAsset().getId(),
                            ruleAction.getDemandDefinition().getId(),
                            LocalDateTime.now(),
                            LocalDateTime.now()).stream().findFirst().orElse(new DemandSchedule());

            if (demandSchedule.getId() == null) {
                demandSchedule.setAsset(ruleAction.getAsset());
                demandSchedule.setDemandDefinition(ruleAction.getDemandDefinition());
                demandSchedule.setDemandStart(currentTime);
            }
            demandSchedule.setDemandStop(
                    LocalDateTime.ofInstant(
                            TimeUtils.offsetPositiveCurrentInstant(ruleAction.getFixedDuration()),
                            ZoneId.systemDefault()));
            demandSchedule.setUpdate(currentTime);

            demandScheduleRepository.save(demandSchedule);
        } else {
            evaluationResult.setExecutedString("empty node - no definition and action.");

        }
    }

    private NotificationSchedule createNotification(RuleDefinition ruleDefinition, EvaluationResult evaluationResult){
        CronExpression cronTrigger = CronExpression.parse(executionCRON);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextExecution = cronTrigger.next(currentTime);
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

        NotificationSchedule notificationSchedule = new NotificationSchedule();
        notificationSchedule.setAsset(ruleDefinition.getMeasurement1().getMeasurement().getAsset());
        notificationSchedule.setDateFrom(currentTime);
        notificationSchedule.setDateTo(nextExecution);
        notificationSchedule.setDefinition(nd);
        notificationSchedule.setMeasurement(ruleDefinition.getMeasurement1().getMeasurement());
        notificationSchedule.setNotificationTimestamp(LocalDateTime.now());
        notificationSchedule = notificationScheduleRepository.save(notificationSchedule);
        return notificationSchedule;
    }

    private String evaluateString(String expression) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("Graal.js");
        return engine.eval(expression).toString();
    }

    private void setEvaluationStringAndData(RuleDefinition ruleDefinition, EvaluationResult evaluationResult) throws RuleEvaluationException {
        List<MeasurementSimplifiedDAO> dataMS1 = dataService.getData(
                ruleDefinition.getMeasurement1().getMeasurement(),
                ruleDefinition.getMeasurement1().getFunction(),
                null,
                TimeUtils.convertLiteralDiffToInstantMillis(ruleDefinition.getMeasurement1().getRangeFrom()),
                Optional.ofNullable(TimeUtils.convertLiteralDiffToInstantMillis(ruleDefinition.getMeasurement1().getRangeTo())));

        Optional<MeasurementSimplifiedDAO> latestMS1 = getLatestMeasurement(dataMS1);
        if(latestMS1.isEmpty())
            throw new RuleEvaluationException("Measurement 1 data could not be retrieved.");

        evaluationResult.setDataResponseMeasurement1(latestMS1.get());

        if(ruleDefinition.getMeasurement2() != null){
            List<MeasurementSimplifiedDAO> dataMS2 = dataService.getData(
                    ruleDefinition.getMeasurement2().getMeasurement(),
                    ruleDefinition.getMeasurement2().getFunction(),
                    null,
                    TimeUtils.convertLiteralDiffToInstantMillis(ruleDefinition.getMeasurement2().getRangeFrom()),
                    Optional.ofNullable(TimeUtils.convertLiteralDiffToInstantMillis(ruleDefinition.getMeasurement2().getRangeTo())));

            Optional<MeasurementSimplifiedDAO> latestMS2 = getLatestMeasurement(dataMS2);
            if(latestMS2.isEmpty())
                throw new RuleEvaluationException("Measurement 2 data could not be retrieved.");

            evaluationResult.setDataResponseMeasurement2(latestMS2.get());
            evaluationResult.setExecutedString(generateExecutableString(ruleDefinition, latestMS1.get(), latestMS2.get()));
        } else {
            String threshold = ruleDefinition.getManualThreshold();

            if(threshold == null)
                throw new RuleEvaluationException("Asset rule configuration missing a threshold value.");

            evaluationResult.setDataResponseMeasurement2(null);
            evaluationResult.setExecutedString(generateExecutableString(ruleDefinition, latestMS1.get(), null));
        }
    }

    private String generateExecutableString(RuleDefinition ruleDefinition, MeasurementSimplifiedDAO latestMS1,
            MeasurementSimplifiedDAO latestMS2){
        StringBuilder sb = new StringBuilder();
        applyMultiplierIfAvailable(sb, ruleDefinition.getMeasurement1().getMultiplier(),
                latestMS1.getFields().get(ruleDefinition.getMeasurement1().getFunction().toLowerCase()));
        sb.append(ruleDefinition.getComparator());
        if(ruleDefinition.getMeasurement2() != null){
            applyMultiplierIfAvailable(sb, ruleDefinition.getMeasurement2().getMultiplier(),
                    latestMS2.getFields().get(ruleDefinition.getMeasurement2().getFunction().toLowerCase()));
        } else {
            sb.append(ruleDefinition.getManualThreshold());
        }
        return sb.toString();
    }

    private void applyMultiplierIfAvailable(StringBuilder sb, Float multiplier, String value){
        if(multiplier != null){
            sb.append("(").append(multiplier).append("*").append(value).append(")");
        } else {
            sb.append(value);
        }
    }

    private Optional<MeasurementSimplifiedDAO> getLatestMeasurement(List<MeasurementSimplifiedDAO> msd){
        return msd.stream().max(Comparator.comparing(m -> m.getFields().get("time")));
    }
}
