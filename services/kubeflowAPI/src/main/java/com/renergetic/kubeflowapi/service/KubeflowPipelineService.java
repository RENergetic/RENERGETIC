package com.renergetic.kubeflowapi.service;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.kubeflowapi.tempcommon.PipelineParameterRepository;
import com.renergetic.kubeflowapi.service.utils.DummyDataGenerator;
import com.renergetic.kubeflowapi.tempcommon.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class KubeflowPipelineService {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @Autowired
    private PipelineRepository pipelineRepository;
    @Autowired
    private PipelineParameterRepository pipelineParameterRepository;
    @Autowired
    private PipelineRunRepository pipelineRunRepository;

    public List<PipelineDefinitionDAO> getAll() {
        Map<String, PipelineDefinitionDAO> kubeflowMap = this.getKubeflowMap();
        return pipelineRepository.findByVisible(true).stream()
                .map((it) -> {
                    //filter also parameters
                    PipelineDefinitionDAO enrichedPipeline = enrichDAO(kubeflowMap, it, false);
                    enrichedPipeline.setParameters(
                            enrichedPipeline.getParameters().entrySet().stream()
                                    .filter(param -> param.getValue().getVisible())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    );
                    return enrichedPipeline;

                }).collect(Collectors.toList());

    }

    public List<PipelineDefinitionDAO> getAllAdmin(Optional<Boolean> visible) {

        HashMap<String, PipelineDefinitionDAO> kubeflowMap = this.getKubeflowMap();
        List<PipelineDefinition> localPipelines;
        var mVisible = visible.orElse(null);
        if (mVisible == null) {
            localPipelines = pipelineRepository.findAll();
        } else if (!mVisible) {
            localPipelines =
                    pipelineRepository.findAll();  //we need to get those visible to filter out the kubeflow pipelines
//            localPipelines = pipelineRepository.findByVisible(false);
        } else {// if (mVisible) {
            localPipelines = pipelineRepository.findByVisible(true);

        }
        localPipelines.forEach(it -> {
            if (mVisible == null || mVisible == it.getVisible()) {
                kubeflowMap.put(it.getPipelineId(), enrichDAO(kubeflowMap, it, true));
            } else {
                kubeflowMap.remove(it.getPipelineId());
            }
        });

        return kubeflowMap.values().stream().toList();


    }

    public PipelineRunDAO getRun(String pipelineId) {
        var wd = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new NotFoundException(
                        "Pipeline: " + pipelineId + " not available outside kubeflow or not exists"));
        if (wd.getPipelineRun() != null) {
            if (wd.getPipelineRun().getEndTime() == null) { //TODO: YAGO -> check kubeflow API only if the task hasnt finished or ended with error,
//            TODO:  TODO: Yago - update the status here and store state in the db
                var run = wd.getPipelineRun();
                var kubeflowrunId = run.getRunId();
                pipelineRunRepository.save(run); //update the db state
            }
            return PipelineRunDAO.create(wd.getPipelineRun());
        }
        return null;

    }

    public PipelineRunDAO startRun(String pipelineId, Map<String, Object> params) {
        var wd = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new NotFoundException(
                        "Pipeline: " + pipelineId + " not available outside kubeflow or not exists"));
        if (wd.getPipelineRun() != null && wd.getPipelineRun().getStartTime() != null && wd.getPipelineRun().getEndTime() == null) {
            //task hasn't finished
            throw new RuntimeException("Current task is still running");
        }
        PipelineRunDAO runDAO = startKubeflowRun(wd, params);
        PipelineRun currentRun = runDAO.mapToEntity();
        pipelineRunRepository.save(currentRun);
        wd.setPipelineRun(currentRun);
        pipelineRepository.save(wd);
        return runDAO;
    }

    public Boolean stopRun(String pipelineId) {
        var wd = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new NotFoundException(
                        "Pipeline: " + pipelineId + " not available outside kubeflow or not exists"));
        if (wd.getPipelineRun() != null && wd.getPipelineRun().getStartTime() != null && wd.getPipelineRun().getEndTime() == null) {
            PipelineRunDAO runDAO = stopKubeflowRun(wd);
            PipelineRun currentRun = runDAO.mapToEntity();
            pipelineRunRepository.save(currentRun);
            wd.setPipelineRun(currentRun);
            pipelineRepository.save(wd);
        } else {
            throw new NotFoundException(
                    " WorkflowRun for: " + pipelineId + " not found , not started or has been already finished");
        }
        return true;
    }

    public boolean setVisibility(String pipelineId) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
        wd.setVisible(true);
        wd = pipelineRepository.save(wd);
        return wd.getVisible();
    }

    public boolean removeVisibility(String pipelineId) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
        wd.setVisible(false);
        wd = pipelineRepository.save(wd);
        return wd.getVisible();
    }


    public Map<String, PipelineParameterDAO> setParameters(String pipelineId,
                                                           Map<String, PipelineParameterDAO> parameters) {
        Map<String, String> kfParameters;
        if (generateDummy) {
            kfParameters = DummyDataGenerator.getParameters(pipelineId);
        } else {
            kfParameters =
                    Collections.emptyMap();//TODO: Yago -> download parameters for the pipeline from the kubeflow API. just key-value dict
        }
        Map<String, PipelineParameterDAO> kubeflowParameters = mapKubeflowParameters(kfParameters);
        List<PipelineParameter> userParams =
                parameters.values().stream().map(PipelineParameterDAO::mapToEntity).collect(Collectors.toList());
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        if (byId.isEmpty()) {
            wd = initPipelineDefinition(pipelineId, null);

        } else {
            wd = byId.get();
            wd.getParameters().forEach(it -> pipelineParameterRepository.delete(it));
        }
        var mPipeline = pipelineRepository.save(wd);
        mPipeline.setParameters(userParams);
        List<PipelineParameter> pipelineParameters = this.mergeParameters(mPipeline, kubeflowParameters);
//        wd.setParameters(pipelineParameters);
        pipelineParameters.forEach(it -> it.setPipelineDefinition(mPipeline));
        Map<String, PipelineParameterDAO> params =
                pipelineParameters.stream().map(it -> pipelineParameterRepository.save(it))
                        .map(PipelineParameterDAO::create)
                        .collect(Collectors.toMap(PipelineParameterDAO::getKey, Function.identity()));


//        pipelineRepository.save(wd);
        return params;
    }

    //#region private
    private List<PipelineParameter> mergeParameters(PipelineDefinition wd,
                                                    Map<String, PipelineParameterDAO> kubeFlowParameters) {
        if (kubeFlowParameters == null) {
            return null;
        }
        if (kubeFlowParameters.isEmpty()) {
            return Collections.emptyList();
        }
//        Optional<PipelineDefinition> byId = workFlowRepository.findById(pipelineId);
//        PipelineDefinition wd;
//        if (byId.isPresent()) {
//            wd = byId.get();
        Map<String, PipelineParameter> wpMap =
                wd.getParameters().stream().collect(Collectors.toMap(PipelineParameter::getKey, o -> o));
        //take exististing parameters only if exists in the kubeflow, otherwise init paremeter object
        List<PipelineParameter> wpList = kubeFlowParameters.entrySet().stream().map(entry ->
        {
            //key exist in the kubeflow - keep current definition
            if (wpMap.containsKey(entry.getKey())) {
                return wpMap.get(entry.getKey());
            }
            PipelineParameter wp = new PipelineParameter();
            wp.setPipelineDefinition(wd);
            wp.setType("string");
            if (entry.getValue().getDefaultValue() != null)
                wp.setDefaultValue(entry.getValue().getDefaultValue());
            wp.setKey(entry.getKey());
            return wp;
        }).collect(Collectors.toList());
//            wd.setParameters(wpList);
        return wpList;
//        } else {
//            wd = initWorkFlowDefinition(pipelineId, kubeFlowParameters);
//
//        }
//        wd = workFlowRepository.save(wd);
//        return wd;
    }

    private PipelineDefinition initPipelineDefinition(String pipelineId) {
        return this.initPipelineDefinition(pipelineId, null);
    }

    private Map<String, PipelineParameterDAO> mapKubeflowParameters(Map<String, String> parameters) {
        Map<String, PipelineParameterDAO> params = parameters.entrySet().stream().map((entry) -> {
            PipelineParameterDAO wp = new PipelineParameterDAO();
            wp.setType("string");
            wp.setDefaultValue(entry.getValue());
            wp.setKey(entry.getKey());
            wp.setVisible(false);
            return wp;
        }).collect(Collectors.toMap(PipelineParameterDAO::getKey, Function.identity()));
        return params;
    }

    private PipelineDefinition initPipelineDefinition(String pipelineId, List<PipelineParameter> parameters) {
        PipelineDefinition wd = new PipelineDefinition(pipelineId);
        wd.setVisible(false);
        if (parameters != null) {
            wd.setParameters(parameters);
        }
        return wd;
    }


    private PipelineRunDAO startKubeflowRun(PipelineDefinition wd, Map<String, Object> params) {

        PipelineDefinitionDAO definitionDAO = PipelineDefinitionDAO.create(wd);
        PipelineRunDAO runDAO;
        if (generateDummy) {
            runDAO = DummyDataGenerator.startKubeflowRun(definitionDAO, params);

        } else {//	 TODO: get and start kubeflow run YAGO:
            runDAO = new PipelineRunDAO();
            runDAO.setParameters(params);
            runDAO.setPipelineDefinitionDAO(definitionDAO);
            //            runDAO.setRunId(runId); -> set id
            // fill the fields of the run object
        }

        // in case of error:             throw new RuntimeException("Pipeline hasn't started");

        return runDAO;
    }

    private PipelineRunDAO stopKubeflowRun(PipelineDefinition wd) {
        PipelineRun workflowRun = wd.getPipelineRun();
        if (generateDummy) {
            workflowRun.setEndTime(DateConverter.toLocalDateTime(DateConverter.now()));

        } else {
            //	 TODO: stop kubeflow run? currently end time is just set Yago
            var kubeflowRunId = workflowRun.getRunId();
            workflowRun.setEndTime(DateConverter.toLocalDateTime(DateConverter.now()));
        }

        return PipelineRunDAO.create(workflowRun);
    }

    private PipelineRunDAO getKubeflowRun(PipelineDefinitionDAO wd) {
        if (generateDummy) {
            return DummyDataGenerator.getKubeflowRun(wd);
        } else {//	 TODO: get kubeflow run
            return null;
        }
    }

    /**
     * Get all kubeflow pipelines
     *
     * @return indexed by pipelineid pipelines
     */
    private HashMap<String, PipelineDefinitionDAO> getKubeflowMap() {

        HashMap<String, PipelineDefinitionDAO> kubeflowMap;
        if (generateDummy) {
            kubeflowMap = DummyDataGenerator.getAllKubeflowWorkflowsMap(15);
        } else {
            //		TODO: list all pipelines in the kubeflow with the paramaters -> Yago
            kubeflowMap = new HashMap<>();
        }
        return kubeflowMap;
    }

    /**
     * @param kubeflowMap kubeflow experiments definitions with input parameters
     * @param item
     * @param update
     * @return
     */
    private PipelineDefinitionDAO enrichDAO(Map<String, PipelineDefinitionDAO> kubeflowMap,
                                            PipelineDefinition item, Boolean update) {
        PipelineDefinitionDAO dao = PipelineDefinitionDAO.create(item);
        ;
        if (kubeflowMap.containsKey(item.getPipelineId())) {
            var kb = kubeflowMap.get(item.getPipelineId());
            //            if (  && item.getPipelineRun() != null) {
            if (item.getPipelineRun() != null && item.getPipelineRun().getEndTime() == null) {
                if (generateDummy) {

                } else {
                    var runId = item.getPipelineRun().getRunId();
                    //           TODO:     verify run id in the kubeflow       -> Yago
                }
            }

            item.setParameters(this.mergeParameters(item, kb.getParameters()));

            dao = PipelineDefinitionDAO.create(item);
            dao.setName(kb.getName());
            dao.setDescription(kb.getDescription());

        }
        return dao;
    }
    //#endregion
}