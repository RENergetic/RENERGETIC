package com.renergetic.ruleevaluationservice.dao;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DataResponse {
    private DataFields dataUsedForComparison;
    private List<Object> rawResult;
}
