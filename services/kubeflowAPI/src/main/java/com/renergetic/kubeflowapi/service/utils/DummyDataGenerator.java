package com.renergetic.kubeflowapi.service.utils;


import com.renergetic.common.dao.PipelineDefinitionDAO;
import com.renergetic.common.dao.PipelineParameterDAO;
import com.renergetic.common.dao.PipelineRunDAO;
import com.renergetic.common.model.PipelineParameter;
import com.renergetic.common.utilities.DateConverter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DummyDataGenerator {

    private static final Random random = new Random();

    public static List<PipelineDefinitionDAO> getAllKubeflowWorkflows() {
        return getAllKubeflowWorkflows(15);
    }

    private static List<PipelineParameter> mapKubeflowParameters(Map<String, String> parameters) {
        List<PipelineParameter> params = parameters.entrySet().stream().map((entry) -> {
            PipelineParameter wp = new PipelineParameter();
            wp.setType("string");
//            wp.setDefaultValue(wp.getDefaultValue());
            wp.setKey(entry.getKey());
            if (entry.getValue() != null)
                wp.setDefaultValue(entry.getValue());
            return wp;
        }).collect(Collectors.toList());
        return params;
    }

    public static List<PipelineDefinitionDAO> getAllKubeflowWorkflows(int size) {
        size = Math.min(10, size);
        return IntStream.range(0, size)
                .mapToObj(i -> {
                    PipelineDefinitionDAO dao = new PipelineDefinitionDAO("pipeline_" + i);
                    dao.setParameters(mapKubeflowParameters(getParameters(dao.getPipelineId())).stream().collect(
                            Collectors.toMap(PipelineParameter::getKey, PipelineParameterDAO::create)));
//                    if (i % 2 == 0) {
//                        HashMap<String, WorkflowParameterDAO> m = new HashMap<>(2);
//                        m.put("param1",
//                                new WorkflowParameterDAO("param1", "default", "string", "Some parameter decription"));
//                        m.put("param2", new WorkflowParameterDAO("param2", null, "string", "parameter 2 decription"));
//                        dao.setParameters(m);
//                    }
                    dao.setName("pipe " + i);
                    return dao;
                }).collect(Collectors.toList());
    }

    public static HashMap<String, String> getParameters(String experimentId) {
        String[] s = experimentId.split("_");
        HashMap<String, String> params = new HashMap<>();
        if (s.length == 2 && Integer.parseInt(s[1]) % 2 == 0) {
            params.put("param1", "default");
            params.put("param2", null);
            params.put("admin_param_1", "secret");
            params.put("admin_param_2", null);
        }
        return params;

    }

    public static Map<String, PipelineDefinitionDAO> getAllKubeflowWorkflowsMap() {
        return getAllKubeflowWorkflowsMap(10);
    }

    public static HashMap<String, PipelineDefinitionDAO> getAllKubeflowWorkflowsMap(int size) {
        return new HashMap<>(getAllKubeflowWorkflows(size).stream()
                .collect(Collectors.toMap(PipelineDefinitionDAO::getPipelineId, Function.identity()))
        );
    }

    public static PipelineRunDAO getKubeflowRun(PipelineDefinitionDAO wd) {
        var ts = DateConverter.now();
        PipelineRunDAO wr = new PipelineRunDAO();
        wr.setRunId("run_" + ts);
        wr.setPipelineDefinitionDAO(wd);
        HashMap<String, Object> m = new HashMap<>(2);
        m.put("param1", ts + "");
        m.put("param2", wd.getPipelineId());
        wr.setParameters(m);
        wr.setStartTime(ts);
        return wr;
    }

    public static PipelineRunDAO startKubeflowRun(PipelineDefinitionDAO wd, Map<String, Object> params) {
        var ts = DateConverter.now();
        PipelineRunDAO wr = new PipelineRunDAO();
        wr.setRunId("run_" + ts);
        wr.setParameters(params);
        wr.setPipelineDefinitionDAO(wd);
        wr.setStartTime(ts);
        wr.setStartTime(ts);
        return wr;
    }
}