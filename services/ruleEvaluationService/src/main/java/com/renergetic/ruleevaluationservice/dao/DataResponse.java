package com.renergetic.ruleevaluationservice.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataResponse {
    private DataFields dataUsedForComparison;
    private List<Object> rawResult;
}
