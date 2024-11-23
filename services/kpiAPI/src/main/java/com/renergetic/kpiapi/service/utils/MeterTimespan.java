package com.renergetic.kpiapi.service.utils;

import com.renergetic.common.utilities.DateConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

@Getter
@Setter
public class MeterTimespan {

//    private Integer meterPeriod;

    long tsFrom;
    long tsTo;

    private MeterTimespan(Integer meterMinPeriod, Long ts) {
        if (ts == null) {
            ts = DateConverter.now();
        }
        var intervalMs = 60000L * meterMinPeriod.longValue();//meterPeriod - timespan in minutes
        this.tsTo = ts - (ts % intervalMs);
        this.tsFrom = this.tsTo - intervalMs;

    }

    public static MeterTimespan init(Integer meterMinPeriod) {
        return MeterTimespan.init(meterMinPeriod, null);
    }

    public static MeterTimespan init(Integer meterMinPeriod, Long ts) {
        return new MeterTimespan(meterMinPeriod, ts);
    }
}
