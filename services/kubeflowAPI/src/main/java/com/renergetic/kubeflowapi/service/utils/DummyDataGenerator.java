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
                    WorkflowDefinitionDAO dao = new WorkflowDefinitionDAO("experiment_"+i);

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
        wr.setRunId("run_"+ts);
        wr.setWorkflowDefinition(wd);
        HashMap<String,Object> m = new HashMap<>(2);
        m.put("param1",ts+"");
        m.put("param2",wd.getExperimentId());
        wr.setParameters(m);
        wr.setStartTime(ts);
        return wr;
    }
}

//    function generatePanelData(informationPanel, predictionWindow) {
//        let measurements = listMeasurements(informationPanel);
//
//        let data = {};
//        for (let m of Object.keys(measurements)) {
//            let value = Math.floor(Math.random() * 150);
//            data[m] = value;
//        }
//        if (predictionWindow == null) return { current: { default: data } };
//        return { prediction: { default: data } };
//    }

/**
 * {
 * "data": {
 * "current": {
 * "default": {
 * "1": 33
 * },
 * "max": {
 * "1": 33
 * }
 * },
 * "prediction": {
 * "default": {
 * "1": 33
 * },
 * "max": {
 * "1": 33
 * }
 * }
 * }
 *///    public static List<DemandScheduleDAO> getDemand(List<DemandDefinition> demands, List<Asset> assets) {
//        long current = (new Date()).getTime();
//
//        return demands.stream().flatMap(
//                def -> assets.stream().map(
//                        asset -> {
//                            DemandSchedule demandSchedule = new DemandSchedule();
//                            demandSchedule.setAsset(asset);
//                            demandSchedule.setDemandDefinition(def);
//                            demandSchedule.setDemandStart(DateConverter.toLocalDateTime(current - 3600000));
//                            demandSchedule.setDemandStop(DateConverter.toLocalDateTime(current + 3600000));
//                            return demandSchedule;
//                        }
//                )
//
//        ).map(DemandScheduleDAO::create).collect(Collectors.toList());
//    }
//    public static DataDAO getDemandData(List<DemandScheduleDAO> demands) {
//        DataDAO data = new DataDAO();
//        List<MeasurementDAOResponse> measurementDAOResponseStream = demands.stream().filter(
//                it -> it.getDemandDefinition().getTile() != null
//        )
//                .flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream()).collect(
//                        Collectors.toList());
//        Map<String, Double> measurementValues = demands.stream().filter(
//                it -> it.getDemandDefinition().getTile() != null
//        )
//                .flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
//                .collect(Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue,
//                        (a1, a2) -> a1));
//
//        data.getCurrent().setLast(measurementValues);
//
//        return data;
//    }

//    public static DataDAO getData(List<Measurement> measurements) {
//        DataDAO data = new DataDAO();
//        Map<String, Double> measurementValues = measurements.stream().collect(
//                Collectors.toMap(it -> it.getId().toString(), DummyDataGenerator::getMeasurementValue, (a1, a2) -> a1));
//        data.getCurrent().setLast(measurementValues);
//        return data;
//    }