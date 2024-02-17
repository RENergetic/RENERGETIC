package com.renergetic.kubeflowapi.service;

import com.renergetic.kubeflowapi.dao.tempcommon.WorkFlowRepositoryTemp;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowDefinition;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowDefinitionDAO;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowRunDAO;
import com.renergetic.kubeflowapi.service.utils.DummyDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class WorkflowService {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @Autowired
    private WorkFlowRepositoryTemp workFlowRepository;

    public List<WorkflowDefinitionDAO> getAll() {
        Map<String, WorkflowDefinitionDAO> kubeflowMap = getKubeflowMap();
        return workFlowRepository.findByVisible(true).stream().map(WorkflowDefinitionDAO::create)
                .map(it -> enrichDAO(kubeflowMap, it)).collect(Collectors.toList());
    }

    public List<WorkflowDefinitionDAO> getAllAdmin(Optional<Boolean> visible) {

        Map<String, WorkflowDefinitionDAO> kubeflowMap = getKubeflowMap();
        if (visible.isEmpty()) {
            workFlowRepository.findAll().stream().map(WorkflowDefinitionDAO::create)
                    .map(it -> enrichDAO(kubeflowMap, it)).forEach(
                            it -> kubeflowMap.put(it.getExperimentId(), it)
                    );
            return kubeflowMap.values().stream().toList();
        } else if (visible.get()) {
            return workFlowRepository.findByVisible(true).stream().map(WorkflowDefinitionDAO::create)
                    .map(it -> enrichDAO(kubeflowMap, it)).collect(Collectors.toList());
        } else {
            workFlowRepository.findAll().stream().map(WorkflowDefinitionDAO::create)
                    .map(it -> enrichDAO(kubeflowMap, it)).forEach(
                            it -> kubeflowMap.put(it.getExperimentId(), it)
                    );
            return kubeflowMap.values().stream().filter(it -> !it.isVisible()).toList();
        }


    }

    public WorkflowRunDAO getRun(String experimentId) {
        var wd = workFlowRepository.findById(experimentId).orElse(new WorkflowDefinition(experimentId));
        return getKubeflowRun(WorkflowDefinitionDAO.create(wd));

    }


    private WorkflowRunDAO getKubeflowRun(WorkflowDefinitionDAO wd) {

        if (generateDummy) {
            return DummyDataGenerator.getKubeflowRun(wd);
        } else {//	 TODO: get kubeflow run
            return null;
        }
    }

    private Map<String, WorkflowDefinitionDAO> getKubeflowMap() {
        Map<String, WorkflowDefinitionDAO> kubeflowMap;
        if (generateDummy) {
            kubeflowMap = DummyDataGenerator.getAllKubeflowWorkflowsMap(15);
        } else {
//		TODO: list all experiments/worflows containing pipelines in the kubeflow -> yago
            kubeflowMap = Collections.emptyMap();
        }
        return kubeflowMap;
    }


    private WorkflowDefinitionDAO enrichDAO(Map<String, WorkflowDefinitionDAO> kubeflowMap,
                                            WorkflowDefinitionDAO item) {


        if (kubeflowMap.containsKey(item.getExperimentId())) {
            var kb = kubeflowMap.get(item.getExperimentId());
            item.setName(kb.getName());
            item.setParameters(kb.getParameters());
            item.setPipelines(kb.getPipelines());
            item.setCurrentRun(kb.getCurrentRun());

        }
        return item;
    }


}
