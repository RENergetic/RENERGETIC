package com.renergetic.kpiapi.config;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
import com.renergetic.kpiapi.service.KPIService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduledProcesses {

    @Autowired
    AbstractMeterDataService meterService;
    
    @Autowired
    KPIService kpiService;

    @Async
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void calculateMeters() {
        List<AbstractMeterDataDAO> data = meterService
                .calculateAndInsertAll(Instant.now().toEpochMilli() - 3600000, Instant.now().toEpochMilli(), null);

        log.info("Abstract meters calculated");
        data.forEach(obj -> obj.getData().forEach((time, value) ->
                log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
            )
        );
    }

    @Async
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void calculateKpis() {
        List<KPIDataDAO> electricityData = kpiService
                .calculateAndInsertAll(Domain.electricity, Instant.now().toEpochMilli() - 3600000, Instant.now().toEpochMilli(), null);

        List<KPIDataDAO> heatData = kpiService
                .calculateAndInsertAll(Domain.electricity, Instant.now().toEpochMilli() - 3600000, Instant.now().toEpochMilli(), null);

        log.info("Electricity KPIs calculated");
        electricityData.forEach(obj -> obj.getData().forEach((time, value) ->
                log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
            )
        );
        log.info("Heat KPIs calculated");
        heatData.forEach(obj -> obj.getData().forEach((time, value) ->
                log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
            )
        );
    }
}
