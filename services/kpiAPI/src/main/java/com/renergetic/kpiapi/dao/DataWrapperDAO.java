package com.renergetic.kpiapi.dao;

import com.renergetic.kpiapi.service.utils.MeterTimespan;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DataWrapperDAO {
    Map<String, String> data;

    String domain;

    MeterTimespan ts;


}
