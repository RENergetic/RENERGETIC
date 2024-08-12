package com.renergetic.kubeflowapi.service;

import com.renergetic.common.dao.PipelineDefinitionDAO;
import com.renergetic.common.dao.PipelineDefinitionPropertyDAO;
import com.renergetic.common.dao.PipelineParameterDAO;
import com.renergetic.common.dao.PipelineRunDAO;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.PipelineDefinition;
import com.renergetic.common.model.PipelineDefinitionProperty;
import com.renergetic.common.model.PipelineParameter;
import com.renergetic.common.model.PipelineRun;
import com.renergetic.common.repository.PipelineDefinitionPropertyRepository;
import com.renergetic.common.repository.PipelineParameterRepository;
import com.renergetic.common.repository.PipelineRepository;
import com.renergetic.common.repository.PipelineRunRepository;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.kubeflowapi.service.utils.DummyDataGenerator;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private PipelineDefinitionPropertyRepository pipelinePropertyRepository;
    @Autowired
    private PipelineRunRepository pipelineRunRepository;
    @Autowired
    private KubeflowService kubeflowService;

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

        return kubeflowMap.values().stream().filter(it -> (mVisible == null || !mVisible) || it.isVisible()).toList();


    }

    public PipelineRunDAO getRun(String pipelineId) {
        var wd = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new NotFoundException(
                        "Pipeline: " + pipelineId + " not available outside kubeflow or not exists"));
        if (wd.getPipelineRun() != null) {
            if (wd.getPipelineRun().getEndTime() == null && !generateDummy) {
                var run = wd.getPipelineRun();
                PipelineRunDAO kubeflowRun = kubeflowService.getRun(run.getRunId());
                run.setState(kubeflowRun.getState());
                if (kubeflowRun.getEndTime() != null)
                    run.setEndTime(kubeflowRun.getEndTime());
                pipelineRunRepository.save(run); //update the db state
            }
            return PipelineRunDAO.create(wd.getPipelineRun());
        }
        return null;

    }

    public List<PipelineRunDAO> listRuns(String pipelineId, Long from, Long to, Long limit, Long offset) {
        return pipelineRunRepository.getPipelineRuns(pipelineId, from, to, offset, limit)
                .stream().map(PipelineRunDAO::create).toList();


    }

    public PipelineRunDAO startRun(String pipelineId, Map<String, Object> params) {
        var wd = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new NotFoundException(
                        "Pipeline: " + pipelineId + " not available outside kubeflow or not exists"));
        if (wd.getPipelineRun() != null && wd.getPipelineRun().getStartTime() != null && wd.getPipelineRun().getEndTime() == null) {
            //task hasn't finished
            PipelineRunDAO run = this.getRun(pipelineId);
            if (run.getEndTime() == null) ;
            throw new RuntimeException("Current task is still running");
        }
        PipelineRunDAO runDAO = startKubeflowRun(wd, params);
        var now = DateConverter.now();
        PipelineRun currentRun = runDAO.mapToEntity();
        currentRun.setInitTime(now);
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
                                                           Map<String, PipelineParameterDAO> parameters) throws ParseException {
        Map<String, String> kfParameters;
        Map<String, PipelineParameterDAO> kubeflowParameters;
        if (generateDummy) {
            kfParameters = DummyDataGenerator.getParameters(pipelineId);
            kubeflowParameters = mapKubeflowParameters(kfParameters);
//todo remove
            PipelineDefinitionDAO pipeline = kubeflowService.getPipeline(pipelineId);
            kubeflowParameters = pipeline.getParameters();
        } else {
            PipelineDefinitionDAO pipeline = kubeflowService.getPipeline(pipelineId);
            kubeflowParameters = pipeline.getParameters();
        }

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

    /**
     * @param wd
     * @param kubeFlowParameters parameters from kubeflwo services
     * @return
     */
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

        } else {

            runDAO = kubeflowService.runPipeline(definitionDAO.getPipelineId(), params);
            runDAO.setParameters(params);
            runDAO.setPipelineDefinitionDAO(definitionDAO);
        }

        // in case of error:             throw new RuntimeException("Pipeline hasn't started");

        return runDAO;
    }

    private PipelineRunDAO stopKubeflowRun(PipelineDefinition wd) {
        PipelineRun workflowRun = wd.getPipelineRun();
        if (generateDummy) {
            workflowRun.setEndTime(DateConverter.now());

        } else {
            //
            if (workflowRun.getEndTime() == null) {

                var kubeflowRunId = workflowRun.getRunId();
                kubeflowService.stopRun(kubeflowRunId);
                var kubeflowRun = kubeflowService.getRun(kubeflowRunId);
                if (kubeflowRun.getEndTime() != null) {
                    workflowRun.setEndTime(kubeflowRun.getEndTime());
                }
                workflowRun.setState(kubeflowRun.getState());
                pipelineRunRepository.save(workflowRun);
            }


//            kubeflowService.stopRun(kubeflowRunId);


        }

        return PipelineRunDAO.create(workflowRun);
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
//
//            try {
//                kubeflowMap = kubeflowService.getPipelines();
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
        } else {

            try {
                kubeflowMap = kubeflowService.getPipelines();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
//        try {
//            kubeflowService.getPipelines();
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
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
                    var run = item.getPipelineRun();
                    var runId = run.getRunId();

                    PipelineRunDAO kbfRun = kubeflowService.getRun(runId);
                    if (kbfRun.getEndTime() != null)
                        run.setEndTime(kbfRun.getEndTime());

                    run.setState(kbfRun.getState());
                    pipelineRunRepository.save(run);
                }
            }

            item.setParameters(this.mergeParameters(item, kb.getParameters()));

            dao = PipelineDefinitionDAO.create(item);
            dao.setName(kb.getName());
            dao.setVersion(kb.getVersion());
            dao.setDescription(kb.getDescription());

        }
        return dao;
    }

    @Transactional
    public PipelineDefinitionPropertyDAO setProperty(String pipelineId, PipelineDefinitionPropertyDAO propertyDAO, Optional<Boolean> unique) {
        if (unique.isPresent() && unique.get()) {
            clearProperty(propertyDAO.getKey());
        }
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition pd;
        if (byId.isPresent()) {
            pd = byId.get();
        } else {
            pd = initPipelineDefinition(pipelineId);
            pipelineRepository.save(pd);
        }
        var property = propertyDAO.mapToEntity();
        pipelinePropertyRepository.getPipelineProperty(pipelineId, propertyDAO.getKey())
                .ifPresent(pipelineDefinitionProperty -> property.setId(pipelineDefinitionProperty.getId()));
        property.setPipelineDefinition(pd);
        pipelinePropertyRepository.save(property);
        return PipelineDefinitionPropertyDAO.create(property);
    }

    @Transactional
    public PipelineDefinitionPropertyDAO setProperty(String pipelineId, String propertyKey, String propertyValue, Optional<Boolean> unique) {
        if (unique.isPresent() && unique.get()) {
            clearProperty(propertyKey);
        }
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        if (byId.isEmpty()) {
            PipelineDefinition pd = initPipelineDefinition(pipelineId);
            pipelineRepository.save(pd);
        }
//        else   pd = byId.get();

        PipelineDefinitionProperty property = pipelinePropertyRepository.getPipelineProperty(pipelineId, propertyKey).orElseThrow(() -> new NotFoundException(
                "Pipeline property (" + pipelineId + "," + propertyKey + ") not exists  "));
        property.setValue(propertyValue);
        pipelinePropertyRepository.save(property);
        return PipelineDefinitionPropertyDAO.create(property);
    }

    public PipelineDefinitionPropertyDAO deleteProperty(String pipelineId, String propertyKey) {
        PipelineDefinitionProperty pipelineDefinitionProperty = pipelinePropertyRepository.getPipelineProperty(pipelineId, propertyKey).orElseThrow(() -> new NotFoundException(
                "Pipeline property (" + pipelineId + "," + propertyKey + ") not exists  "));
        pipelinePropertyRepository.deletePipelineProperty(pipelineId, propertyKey);
        return PipelineDefinitionPropertyDAO.create(pipelineDefinitionProperty);
    }

    public void clearProperty(String propertyKey) {

        pipelinePropertyRepository.deletePipelineProperty(propertyKey);

    }

    public List<PipelineDefinitionDAO> getByProperty(String propertyKey, String propertyValue) {
        return pipelineRepository.findByProperty(propertyKey, propertyValue).stream().map(PipelineDefinitionDAO::create).toList();

    }
    //#endregion
}