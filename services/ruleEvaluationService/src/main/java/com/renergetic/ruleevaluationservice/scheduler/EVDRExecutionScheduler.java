package com.renergetic.ruleevaluationservice.scheduler;

import com.renergetic.ruleevaluationservice.exception.ConfigurationError;
import com.renergetic.ruleevaluationservice.service.EVDRService;
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
public class EVDRExecutionScheduler {

    @Autowired
    EVDRService evdrService;
    @Scheduled(cron = "${ev-dr.executionCRON}")
    @Transactional(propagation= Propagation.REQUIRED)
    public void executeRules() throws ConfigurationError {
        //evdrService.evaluateEVDR();
    }

}
