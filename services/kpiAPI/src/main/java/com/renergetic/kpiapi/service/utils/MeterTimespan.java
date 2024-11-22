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

    private MeterTimespan(Integer meterPeriod, Long ts) {
        if (ts == null) {
            ts = DateConverter.now();// Instant.now().toEpochMilli();
        }
        var interval = 60000l * meterPeriod;
        this.tsTo = ts - (ts % interval);
        this.tsFrom = this.tsTo - interval;

    }

    public static MeterTimespan init(Integer meterPeriod) {
        return MeterTimespan.init(meterPeriod,null);
    }

    public static MeterTimespan init(Integer meterPeriod, Long ts) {
        return new MeterTimespan(meterPeriod, ts);
    }
}
