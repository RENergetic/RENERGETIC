package com.renergetic.kpiapi.scheduler;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
import com.renergetic.kpiapi.service.KPIService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private AbstractMeterDataService meterService;
    
    @Autowired
    private KPIService kpiService;
    
    @Value("${scheduled.abstrac-meter.period}")
    private Integer meterPeriod;
    
    @Value("${scheduled.kpi.period}")
    private Integer kpiPeriod;

    @Async
    @Scheduled(fixedDelayString = "${scheduled.abstrac-meter.period}", timeUnit = TimeUnit.MINUTES)
    public void calculateMeters() {
        var tsFrom = Instant.now().toEpochMilli();
        var to = Instant.now().toEpochMilli();
        List<AbstractMeterDataDAO> data = meterService
                .calculateAndInsertAll(tsFrom - (60000 * meterPeriod), to, null);

        log.info(String.format("Abtract meters calculated (Period: %d minutes)", meterPeriod));
        data.forEach(obj -> obj.getData().forEach((time, value) ->
                log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
            )
        );
    }

    @Async
    @Scheduled(fixedDelayString = "${scheduled.kpi.period}", timeUnit = TimeUnit.MINUTES)
    public void calculateKpis() {
        List<KPIDataDAO> electricityData = kpiService
                .calculateAndInsertAll(Domain.electricity, Instant.now().toEpochMilli() - (60000 * kpiPeriod), Instant.now().toEpochMilli(), null);

        List<KPIDataDAO> heatData = kpiService
                .calculateAndInsertAll(Domain.heat, Instant.now().toEpochMilli() - (60000 * kpiPeriod), Instant.now().toEpochMilli(), null);

        log.info(String.format("Electricity KPIs calculated (Period: %d minutes)", kpiPeriod));
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
