package com.renergetic.ruleevaluationservice.scheduler;

import com.renergetic.ruleevaluationservice.exception.ConfigurationError;
import com.renergetic.ruleevaluationservice.service.EVDRService;
import com.renergetic.ruleevaluationservice.service.MeasurementAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MeasurementAggregationExecutionScheduler {

    @Autowired
    MeasurementAggregationService measurementAggregationService;
    @Scheduled(cron = "${me-aggr.executionCRON}")
    @Transactional(propagation= Propagation.REQUIRED)
    public void executeRules() throws ConfigurationError {
        measurementAggregationService.aggregateAll();
    }

}
