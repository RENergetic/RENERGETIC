package com.renergetic.ruleevaluationservice.dao;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

@Getter
@Setter
public class DataResponse {
    private DataFields dataUsedForComparison;
    private JSONArray rawResult;
}
