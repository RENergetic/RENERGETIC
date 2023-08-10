package com.renergetic.kpiapi.config;

import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.service.AbstractMeterDataService;
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

    @Async
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void calculateMeters() {
        List<AbstractMeterDataDAO> data = meterService
                .calculateAndInsertAll(Instant.now().toEpochMilli() - 2000, Instant.now().toEpochMilli(), null);

        log.info("Abstract meters calculated");
        data.forEach(obj -> obj.getData().forEach((time, value) ->
                log.info(String.format(" - %s for domain %s: %f.2 at %d", obj.getName(), obj.getDomain(), value, time))
            )
        );
    }
}
