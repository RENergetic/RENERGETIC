package com.renergetic.kpiapi.scheduler;

import com.renergetic.common.model.Domain;
import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.dao.KPIDataDAO;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
import com.renergetic.kpiapi.service.KPIService;

import com.renergetic.kpiapi.service.kpi.EP;
import com.renergetic.kpiapi.service.kpi.ESC;
import com.renergetic.kpiapi.service.kpi.ESS;
import com.renergetic.kpiapi.service.utils.MeterTimespan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduledProcesses {

    private static final String LOG_FORMAT = " - %s for domain %s: %.2f at %d";

    private static Integer nextKpiCalculation = 0;

    @Autowired
    private AbstractMeterDataService meterService;

    @Autowired
    private KPIService kpiService;

    @Value("${scheduled.calculation.period}")
    private Integer meterPeriod;

    @Value("${scheduled.kpi.frequency}")
    private Integer kpiFrequency;

    @Scheduled(fixedDelayString = "${scheduled.calculation.period}", timeUnit = TimeUnit.MINUTES)
    public void calculateKpisAndAbstractMeters() {
        var ts = MeterTimespan.init(meterPeriod);
//        long tsNow = Instant.now().toEpochMilli();
//        long tsFrom = tsNow - 60000 * meterPeriod;

        calcAbstractMeter(ts);

        // KPIs CALCULATION
        if (nextKpiCalculation.equals(kpiFrequency - 1)) {
            log.info("Start Calculate KPIs ");
            List<KPIDataDAO> electricityData = kpiService
                    .calculateAndInsertAll(Domain.electricity, ts.getTsFrom(), ts.getTsTo(), ts.getTsTo());
            List<KPIDataDAO> heatData = kpiService
                    .calculateAndInsertAll(Domain.heat, ts.getTsFrom(), ts.getTsTo(), ts.getTsTo());
            //TODO: comments if its not calculating properly the following KPIS
            electricityData.forEach(obj -> obj.getData().forEach((time, value) ->
                            log.info(String.format(LOG_FORMAT, obj.getName(), obj.getDomain(), value, time))
                    )
            );
            log.info(String.format("Electricity KPIs calculated (Period: %d minutes)", meterPeriod));
            log.info("Heat KPIs calculated");
            heatData.forEach(obj -> obj.getData().forEach((time, value) ->
                            log.info(String.format(LOG_FORMAT, obj.getName(), obj.getDomain(), value, time))
                    )
            );

            try {
                List<KPIDataDAO> allDomain = kpiService
                        .calculateAndInsert(Domain.none, List.of(ESS.Instance, ESC.Instance, EP.Instance), ts.getTsFrom(), ts.getTsTo(), ts.getTsTo());
                log.info("All domain KPIs calculated");
                allDomain.forEach(obj -> obj.getData().forEach((time, value) ->
                                log.info(String.format(LOG_FORMAT, obj.getName(), obj.getDomain(), value, time))
                        )
                );
            } catch (Exception ex) {
                log.error("Error while calculating all domain KPI" + ex.getMessage());

            }

            nextKpiCalculation = 0;
        } else {
            nextKpiCalculation++;
        }
    }

    private void calcAbstractMeter(MeterTimespan ts) {
        List<AbstractMeterDataDAO> data = meterService
                .calculateAndInsertAll(ts);

        log.info(String.format("Abstract meters calculated (Period: %d minutes)", meterPeriod));
        data.forEach(obj -> obj.getData().forEach((time, value) ->
                        log.info(String.format(LOG_FORMAT, obj.getName(), obj.getDomain(), value, time))
                )
        );
    }
}
