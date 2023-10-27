package com.renergetic.ruleevaluationservice.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementSimplifiedDAO {
    String measurement;
    Map<String, String> fields;
    Map<String, String> tags;
}
