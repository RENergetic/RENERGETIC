package com.renergetic.kpiapi.scheduler;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
import com.renergetic.kpiapi.service.KPIService;

import com.renergetic.kpiapi.service.kpi.ESS;
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
        var tsNow = Instant.now().toEpochMilli();
//        var tsNow = ( (int)(Instant.now().toEpochMilli()/60000)*60000); round to minutes ? TODO:
        var tsFrom = tsNow - 60000 * meterPeriod;
        List<AbstractMeterDataDAO> data = meterService
                .calculateAndInsertAll(tsFrom, tsNow, tsNow);

        log.info(String.format("Abtract meters calculated (Period: %d minutes)", meterPeriod));
        data.forEach(obj -> obj.getData().forEach((time, value) ->
                        log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
                )
        );
    }

    @Async
    @Scheduled(fixedDelayString = "${scheduled.kpi.period}", timeUnit = TimeUnit.MINUTES)
    public void calculateKpis() {
        var tsNow = Instant.now().toEpochMilli();
//        var tsNow = ( (int)(Instant.now().toEpochMilli()/60000)*60000); round to minutes ? TODO:
        var tsFrom = tsNow - 60000 * meterPeriod;
        List<KPIDataDAO> electricityData = kpiService
                .calculateAndInsertAll(Domain.electricity, tsFrom, tsNow, tsNow);

        List<KPIDataDAO> heatData = kpiService
                .calculateAndInsertAll(Domain.heat, tsFrom, tsNow, tsNow);
//TODO: comments if its not calculating properly the following KPIS
        List<KPIDataDAO> allDomain = kpiService
                .calculateAndInsert(Domain.none, List.of(ESS.Instance), tsFrom, tsNow, tsNow);

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
        log.info("All domain KPIs calculated");
        allDomain.forEach(obj -> obj.getData().forEach((time, value) ->
                        log.info(String.format(" - %s for domain %s: %.2f at %d", obj.getName(), obj.getDomain(), value, time))
                )
        );
    }
}
