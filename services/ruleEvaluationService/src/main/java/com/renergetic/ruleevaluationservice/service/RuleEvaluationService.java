package com.renergetic.ruleevaluationservice.service;

import com.renergetic.ruleevaluationservice.model.AssetRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleEvaluationService {

    @Autowired
    DataService dataService;

    public String evaluateRule(AssetRule assetRule){
        /*
            TODO:
                1. Fetch data for measurement 1 (using function and group, from should be now - group * 2)
                2. If measurement 2, fetch data for measurement 2 (using function and group, from should be now - group * 2)
                    > Compare both last group, what to do if one is null ?
                    > Trigger notification if needed.
                2b. If from config, get threshold from asset detail, else from manual field
                    > Compare last group, what to do if null ?
                    > Trigger notification if needed.
         */
        dataService.getData()
    }
}
