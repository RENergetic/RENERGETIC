package com.renergetic.kubeflowapi.service;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.kubeflowapi.repository.WorkFlowParameterRepository;
import com.renergetic.kubeflowapi.service.utils.DummyDataGenerator;
import com.renergetic.common.model.WorkflowDefinition;
import com.renergetic.common.model.WorkflowParameter;
import com.renergetic.common.repository.WorkFlowRepository;
import com.renergetic.common.repository.WorkFlowRunRepository;
import com.renergetic.common.dao.WorkflowDefinitionDAO;
import com.renergetic.common.dao.WorkflowParameterDAO;
import com.renergetic.common.dao.WorkflowRunDAO;
import com.renergetic.common.model.WorkflowRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class WorkflowService {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @Autowired
    private WorkFlowRepository workFlowRepository;
    @Autowired
    private WorkFlowParameterRepository workFlowParameterRepository;
    @Autowired
    private WorkFlowRunRepository workFlowRunRepository;

    public List<WorkflowDefinitionDAO> getAll() {
        Map<String, WorkflowDefinitionDAO> kubeflowMap = this.getKubeflowMap();
        return workFlowRepository.findByVisible(true).stream()
                .map(it -> enrichDAO(kubeflowMap, it, false)).collect(Collectors.toList());
    }

    public List<WorkflowDefinitionDAO> getAllAdmin(Optional<Boolean> visible) {

        Map<String, WorkflowDefinitionDAO> kubeflowMap = this.getKubeflowMap();
        List<WorkflowDefinition> wdList;
        if (visible.isEmpty() || !visible.get()) {
            wdList = workFlowRepository.findAll();
        } else {// if (visible.get()) {
            wdList = workFlowRepository.findByVisible(true);
        }
        wdList.stream().map(it -> enrichDAO(kubeflowMap, it, true)).forEach(
                it -> kubeflowMap.put(it.getExperimentId(), it)
        );
        var s = kubeflowMap.values().stream();
        if (visible.isEmpty()) return s.toList();
        return s.filter(it -> visible.get() == it.isVisible()).toList();

    }

    public WorkflowRunDAO getRun(String experimentId) {
        var wd = workFlowRepository.findById(experimentId)
                .orElseThrow(() -> new NotFoundException(
                        "Experiment: " + experimentId + " not available outside kubeflow or not exists"));
        if (wd.getWorkflowRun() != null) {
            return WorkflowRunDAO.create(wd.getWorkflowRun());
        }
        return null;

    }

    public WorkflowRunDAO startRun(String experimentId, Map<String, Object> params) {
        var wd = workFlowRepository.findById(experimentId)
                .orElseThrow(() -> new NotFoundException(
                        "Experiment: " + experimentId + " not available outside kubeflow or not exists"));
        if (wd.getWorkflowRun() != null && wd.getWorkflowRun().getStartTime() != null && wd.getWorkflowRun().getEndTime() == null) {
            //task hasn't finished
            //TODO: raise exception
        }
        WorkflowRunDAO runDAO = startKubeflowRun(wd, params);
        WorkflowRun currentRun = runDAO.mapToEntity();
        workFlowRunRepository.save(currentRun);
        wd.setWorkflowRun(currentRun);
        workFlowRepository.save(wd);
        return runDAO;
    }

    public Boolean stopRun(String experimentId) {
        var wd = workFlowRepository.findById(experimentId)
                .orElseThrow(() -> new NotFoundException(
                        "Experiment: " + experimentId + " not available outside kubeflow or not exists"));
        if (wd.getWorkflowRun() != null && wd.getWorkflowRun().getStartTime() != null && wd.getWorkflowRun().getEndTime() == null) {
            WorkflowRunDAO runDAO = stopKubeflowRun(wd);
            WorkflowRun currentRun = runDAO.mapToEntity();
            workFlowRunRepository.save(currentRun);
            wd.setWorkflowRun(currentRun);
            workFlowRepository.save(wd);
        } else {
            throw new NotFoundException(
                    " WorkflowRun for: " + experimentId + " not found , not started or has been already finished");
        }
        return true;
    }

    public boolean setVisibility(String experimentId) {
        Optional<WorkflowDefinition> byId = workFlowRepository.findById(experimentId);
        WorkflowDefinition wd;
        wd = byId.orElseGet(() -> initWorkFlowDefinition(experimentId));
        wd.setVisible(true);
        wd = workFlowRepository.save(wd);
        return wd.getVisible();
    }

    public boolean removeVisibility(String experimentId) {
        Optional<WorkflowDefinition> byId = workFlowRepository.findById(experimentId);
        WorkflowDefinition wd;
        wd = byId.orElseGet(() -> initWorkFlowDefinition(experimentId));
        wd.setVisible(false);
        wd = workFlowRepository.save(wd);
        return wd.getVisible();
    }

    public List<WorkflowParameter> mergeParameters(WorkflowDefinition wd,
                                                   Map<String, WorkflowParameterDAO> kubeFlowParameters) {
        if (kubeFlowParameters == null) {
            return null;
        }
        if (kubeFlowParameters.isEmpty()) {
            return Collections.emptyList();
        }
//        Optional<WorkflowDefinition> byId = workFlowRepository.findById(experimentId);
//        WorkflowDefinition wd;
//        if (byId.isPresent()) {
//            wd = byId.get();
        Map<String, WorkflowParameter> wpMap =
                wd.getParameters().stream().collect(Collectors.toMap(WorkflowParameter::getKey, o -> o));
        //take exististing parameters only if exists in the kubeflow, otherwise init paremeter object
        List<WorkflowParameter> wpList = kubeFlowParameters.entrySet().stream().map(entry ->
        {
            //key exist in the kubeflow - keep current definition
            if (wpMap.containsKey(entry.getKey())) {
                return wpMap.get(entry.getKey());
            }
            WorkflowParameter wp = new WorkflowParameter();
            wp.setWorkflowDefinition(wd);
            wp.setType("string");
            if (entry.getValue().getDefaultValue() != null)
                wp.setDefaultValue(entry.getValue().getDefaultValue());
            wp.setKey(entry.getKey());
            return wp;
        }).collect(Collectors.toList());
//            wd.setParameters(wpList);
        return wpList;
//        } else {
//            wd = initWorkFlowDefinition(experimentId, kubeFlowParameters);
//
//        }
//        wd = workFlowRepository.save(wd);
//        return wd;
    }

    public Map<String, WorkflowParameterDAO> setParameters(String experimentId,
                                                           Map<String, WorkflowParameterDAO> parameters) {
        Map<String, String> kfParameters;
        if (generateDummy) {
            kfParameters = DummyDataGenerator.getParameters(experimentId);
        } else {
            kfParameters = Collections.emptyMap();//TODO: yago -> download parameters from the kubeflow API
        }
        Map<String, WorkflowParameterDAO> kubeflowParameters = mapKubeflowParameters(kfParameters);
        List<WorkflowParameter> userParams =
                parameters.values().stream().map(WorkflowParameterDAO::mapToEntity).collect(Collectors.toList());
        Optional<WorkflowDefinition> byId = workFlowRepository.findById(experimentId);
        WorkflowDefinition wd;
        if (byId.isEmpty()) {
            wd = initWorkFlowDefinition(experimentId, userParams);
        } else {
            wd = byId.get();
            wd.setParameters(userParams);
        }
        List<WorkflowParameter> workflowParameters = this.mergeParameters(wd, kubeflowParameters);
        WorkflowDefinition savedWD = workFlowRepository.save(wd);
        workflowParameters.forEach(it -> it.setWorkflowDefinition(savedWD));
        return workflowParameters.stream().map(it -> workFlowParameterRepository.save(it))
                .map(WorkflowParameterDAO::create)
                .collect(Collectors.toMap(WorkflowParameterDAO::getKey, Function.identity()));


    }

    private WorkflowDefinition initWorkFlowDefinition(String experimentId) {
        return this.initWorkFlowDefinition(experimentId, null);
    }

    private Map<String, WorkflowParameterDAO> mapKubeflowParameters(Map<String, String> parameters) {
        Map<String, WorkflowParameterDAO> params = parameters.entrySet().stream().map((entry) -> {
            WorkflowParameterDAO wp = new WorkflowParameterDAO();
            wp.setType("string");
            wp.setDefaultValue(entry.getValue());
            wp.setKey(entry.getKey());
            return wp;
        }).collect(Collectors.toMap(WorkflowParameterDAO::getKey, Function.identity()));
        return params;
    }

    private WorkflowDefinition initWorkFlowDefinition(String experimentId, List<WorkflowParameter> parameters) {
        WorkflowDefinition wd = new WorkflowDefinition(experimentId);
        wd.setVisible(false);
        if (parameters != null) {
            wd.setParameters(parameters);
        }
        return wd;
    }


    private WorkflowRunDAO startKubeflowRun(WorkflowDefinition wd, Map<String, Object> params) {
//TODO: check if experiment is already running
        WorkflowDefinitionDAO definitionDAO = WorkflowDefinitionDAO.create(wd);
        WorkflowRunDAO runDAO;
        if (generateDummy) {
            runDAO = DummyDataGenerator.startKubeflowRun(definitionDAO, params);

        } else {//	 TODO: get kubeflow run
            runDAO = null;
        }
        if (runDAO == null) {
            runDAO = new WorkflowRunDAO();
//            todo: log information about failurre start
        }
        return runDAO;
    }

    private WorkflowRunDAO stopKubeflowRun(WorkflowDefinition wd) {
        WorkflowRun workflowRun = wd.getWorkflowRun();
        ;
        if (generateDummy) {
            workflowRun.setEndTime(DateConverter.toLocalDateTime(DateConverter.now()));

        } else {
            //	 TODO: stop kubeflow run? currently end time is just set
            workflowRun.setEndTime(DateConverter.toLocalDateTime(DateConverter.now()));
        }

        return WorkflowRunDAO.create(workflowRun);
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
//		TODO: list all experiments/worflows containing pipelines in the kubeflow with the paramaters -> yago
            kubeflowMap = Collections.emptyMap();
        }
        return kubeflowMap;
    }

    /**
     * @param kubeflowMap kubeflow experiments definitions with input parameters
     * @param item
     * @param update
     * @return
     */
    private WorkflowDefinitionDAO enrichDAO(Map<String, WorkflowDefinitionDAO> kubeflowMap,
                                            WorkflowDefinition item, Boolean update) {
        WorkflowDefinitionDAO dao = WorkflowDefinitionDAO.create(item);
        ;
        if (kubeflowMap.containsKey(item.getExperimentId())) {
            var kb = kubeflowMap.get(item.getExperimentId());
            if (kb.getCurrentRun() == null && item.getWorkflowRun() != null) {
                if (!generateDummy) {
                    //           TODO:     verify run id in the kubeflow       -> yago
                }
            } else if (kb.getCurrentRun() != null) {
                item.setWorkflowRun(kb.getCurrentRun().mapToEntity());
            }
//            if (update) {
//                item.getParameters().forEach(it -> workFlowParameterRepository.delete(it));
//                item.setParameters(this.mergeParameters(item, kb.getParameters()));
//                item.getParameters().forEach(it -> workFlowParameterRepository.save(it));
//                workFlowRepository.save(item);
//            } else {
            item.setParameters(this.mergeParameters(item, kb.getParameters()));
//            }
            dao = WorkflowDefinitionDAO.create(item);
            dao.setName(kb.getName());
            dao.setPipelines(kb.getPipelines());
        }
        return dao;
    }


}