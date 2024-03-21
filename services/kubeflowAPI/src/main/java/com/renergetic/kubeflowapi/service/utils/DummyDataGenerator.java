package com.renergetic.kubeflowapi.service.utils;

//import com.renergetic.common.dao.*;

import com.renergetic.common.dao.*;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.common.repository.RecommendationRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.controller.KubeflowController;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowDefinitionDAO;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowParameter;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowParameterDAO;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowRunDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class DummyDataGenerator {

    private static final Random random = new Random();

    public static List<WorkflowDefinitionDAO> getAllKubeflowWorkflows() {
        return getAllKubeflowWorkflows(15);
    }

    public static List<WorkflowDefinitionDAO> getAllKubeflowWorkflows(int size) {
        size = Math.min(10, size);
        return IntStream.range(0, size)
                .mapToObj(i -> {
                    WorkflowDefinitionDAO dao = new WorkflowDefinitionDAO("experiment_" + i);
                    if (i % 2 == 0) {
                        HashMap<String, WorkflowParameterDAO> m = new HashMap<>(2);
                        m.put("param1",
                                new WorkflowParameterDAO("param1", "default", "string", "Some parameter decription"));
                        m.put("param2", new WorkflowParameterDAO("param2", null, "string", "parameter 2 decription"));
                        dao.setParameters(m);
                    }

                    return dao;
                }).collect(Collectors.toList());
    }

    public static Map<String, WorkflowDefinitionDAO> getAllKubeflowWorkflowsMap() {
        return getAllKubeflowWorkflowsMap(10);
    }

    public static Map<String, WorkflowDefinitionDAO> getAllKubeflowWorkflowsMap(int size) {
        return getAllKubeflowWorkflows(size).stream()
                .collect(Collectors.toMap(WorkflowDefinitionDAO::getExperimentId, Function.identity()));
    }

    public static WorkflowRunDAO getKubeflowRun(WorkflowDefinitionDAO wd) {
        var ts = DateConverter.now();
        WorkflowRunDAO wr = new WorkflowRunDAO();
        wr.setRunId("run_" + ts);
        wr.setWorkflowDefinition(wd);
        HashMap<String, Object> m = new HashMap<>(2);
        m.put("param1", ts + "");
        m.put("param2", wd.getExperimentId());
        wr.setParameters(m);
        wr.setStartTime(ts);
        return wr;
    }

    public static WorkflowRunDAO startKubeflowRun(WorkflowDefinitionDAO wd, Map<String, Object> params) {
        var ts = DateConverter.now();
        WorkflowRunDAO wr = new WorkflowRunDAO();
        wr.setRunId("run_" + ts);
        wr.setParameters(params);
        wr.setStartTime(ts);
        wr.setWorkflowDefinition(wd);
        wr.setStartTime(ts);
        return wr;
    }
}
