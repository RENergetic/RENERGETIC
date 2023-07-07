package com.renergetic.ruleevaluationservice.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

import java.util.HashMap;

@Getter
@Setter
public class DataResponse {
    private DataFields dataUsedForComparison;
    private JSONArray rawResult;
}
