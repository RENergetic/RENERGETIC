package com.renergetic.kpiapi.dao;

import com.renergetic.kpiapi.service.utils.MeterTimespan;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class DataWrapperDAO {
    HashMap<String, String> data;

    String domain;

    MeterTimespan ts;


}
