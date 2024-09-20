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
import com.renergetic.common.repository.*;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.kubeflowapi.service.utils.DummyDataGenerator;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.smallrye.config.ConfigLogging.log;


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
    private InformationPanelRepository informationPanelRepository;
    @Autowired
    private PipelineRunRepository pipelineRunRepository;
    @Autowired
    private KubeflowService kubeflowService;

    Lock runlock = new ReentrantLock();

    public List<PipelineDefinitionDAO> getAll() throws IllegalAccessException {
        Map<String, PipelineDefinitionDAO> kubeflowMap = this.getKubeflowMap();
        return pipelineRepository.findByVisible(true).stream()
                .map((it) -> {
                    //filter also parameters
                    PipelineDefinitionDAO enrichedPipeline = null;
                    try {
                        enrichedPipeline = enrichDAO(kubeflowMap.get(it.getPipelineId()), it, false);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    enrichedPipeline.setParameters(
                            enrichedPipeline.getParameters().entrySet().stream()
                                    .filter(param -> param.getValue().getVisible())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    );
                    return enrichedPipeline;

                }).collect(Collectors.toList());

    }

    public List<PipelineDefinitionDAO> getAllAdmin(Optional<Boolean> visible) throws IllegalAccessException {

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
//            if(kubeflowMap)
            var kbfPipeline = kubeflowMap.get(it.getPipelineId());
            if (kbfPipeline != null && !kbfPipeline.getName().equals(it.getName())) {
                //sync name
                it.setName(kbfPipeline.getName());
                pipelineRepository.save(it);

            }
            if (mVisible == null || mVisible == it.getVisible()) {
                try {
                    kubeflowMap.put(it.getPipelineId(), enrichDAO(kbfPipeline, it, true));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                kubeflowMap.remove(it.getPipelineId());
            }
        });

        return kubeflowMap.values().stream().filter(it -> (mVisible == null || !mVisible) || it.isVisible()).toList();


    }

    public PipelineRunDAO getRun(String pipelineId) throws IllegalAccessException {
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


        try {
            if (runlock.tryLock(30000, TimeUnit.MILLISECONDS)) {
                try {

                    assertRunningSync(wd);

                    var kfPipeline = kubeflowService.getPipeline(pipelineId);
                    PipelineRunDAO runDAO = startKubeflowRun(wd, params);
                    var now = DateConverter.now();
                    PipelineRun currentRun = runDAO.mapToEntity();
                    currentRun.setInitTime(now);
                    pipelineRunRepository.save(currentRun);
                    wd.setPipelineRun(currentRun);
                    wd.setName(kfPipeline.getName());
                    pipelineRepository.save(wd);
                    return runDAO;
                } catch (ParseException e) {
                    throw new RuntimeException("Invalid pipeline: " + pipelineId);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } finally {
                    runlock.unlock();
                }
            } else {
                throw new RuntimeException("Timeout running Pipeline: " + pipelineId);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public Boolean stopRun(String pipelineId) throws IllegalAccessException {
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

    public String setLabel(String pipelineId, String label) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
//        wd.setVisible(true);
        wd.setLabel(label);
        wd = pipelineRepository.save(wd);
        return wd.getLabel();
    }

    public boolean removeVisibility(String pipelineId) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
        wd.setVisible(false);
        wd = pipelineRepository.save(wd);
        return wd.getVisible();
    }


    public PipelineDefinitionDAO setPanel(String pipelineId, Long panelId) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
        var panel = informationPanelRepository.findById(panelId).orElseThrow(() -> new NotFoundException("Panel not found " + panelId));
        wd.setInformationPanel(panel);
        pipelineRepository.save(wd);
        return PipelineDefinitionDAO.create(wd);
    }

    public PipelineDefinitionDAO removePanel(String pipelineId) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition wd;
        wd = byId.orElseGet(() -> initPipelineDefinition(pipelineId));
        wd.setInformationPanel(null);
        pipelineRepository.save(wd);
        return PipelineDefinitionDAO.create(wd);
    }

    public Map<String, PipelineParameterDAO> setParameters(String pipelineId,
                                                           Map<String, PipelineParameterDAO> parameters) throws ParseException, IllegalAccessException {
        Map<String, String> kfParameters;
        Map<String, PipelineParameterDAO> kubeflowParameters;
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
        PipelineDefinitionDAO kfPipeline;
        if (generateDummy) {
            kfParameters = DummyDataGenerator.getParameters(pipelineId);
            kubeflowParameters = mapKubeflowParameters(kfParameters);
            kfPipeline = kubeflowService.getPipeline(pipelineId);
        } else {
            kfPipeline = kubeflowService.getPipeline(pipelineId);
            kubeflowParameters = kfPipeline.getParameters();
        }
        wd.setName(kfPipeline.getName());
        var mPipeline = pipelineRepository.save(wd);
        mPipeline.setParameters(userParams);
        List<PipelineParameter> pipelineParameters = this.mergeParameters(mPipeline, kubeflowParameters);
//        wd.setParameters(pipelineParameters);
        pipelineParameters.forEach(it -> it.setPipelineDefinition(mPipeline));
        Map<String, PipelineParameterDAO> params =
                pipelineParameters.stream().map(it -> pipelineParameterRepository.save(it))
                        .map(PipelineParameterDAO::create)
                        .collect(Collectors.toMap(PipelineParameterDAO::getKey, Function.identity()));


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


    private PipelineRunDAO startKubeflowRun(PipelineDefinition wd, Map<String, Object> params) throws IllegalAccessException {

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

    private PipelineRunDAO stopKubeflowRun(PipelineDefinition wd) throws IllegalAccessException {
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
    private HashMap<String, PipelineDefinitionDAO> getKubeflowMap() throws IllegalAccessException {

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
     * @param kbfPipeline kubeflow pipeline definition with input parameters
     * @param item        local pipeline
     * @param update
     * @return
     */
    private PipelineDefinitionDAO enrichDAO(@Nullable PipelineDefinitionDAO kbfPipeline,
                                            PipelineDefinition item, Boolean update) throws IllegalAccessException {
        PipelineDefinitionDAO dao = PipelineDefinitionDAO.create(item);
        ;
        if (kbfPipeline != null) {
//            var kb = kubeflowMap.get(item.getPipelineId());
            if (item.getPipelineRun() != null && item.getPipelineRun().getEndTime() == null) {
                if (generateDummy) { //
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
            item.setParameters(this.mergeParameters(item, kbfPipeline.getParameters()));

            dao = PipelineDefinitionDAO.create(item);
//            if(kbfPipeline.getName().equals())
            dao.setName(kbfPipeline.getName());
            dao.setVersion(kbfPipeline.getVersion());
            dao.setDescription(kbfPipeline.getDescription());

        }
        return dao;
    }


    private void assertRunningSync(PipelineDefinition wd) {
        try {
            if (runlock.tryLock(15000L, TimeUnit.MILLISECONDS)) {
                try {
                    this.assertRunning(wd);
                } finally {
                    runlock.unlock();
                }
            }
        } catch (InterruptedException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertRunning(PipelineDefinition wd) throws IllegalAccessException {


        if (wd.getPipelineRun() != null && wd.getPipelineRun().getStartTime() != null && wd.getPipelineRun().getEndTime() == null) {
            //task hasn't finished
            PipelineRunDAO run = this.getRun(wd.getPipelineId());
            if (run.getEndTime() == null)
                throw new RuntimeException("Current task is still running");
        }
    }

    //#endregion

    //#region properties
    @Transactional
    public PipelineDefinitionPropertyDAO setProperty(String pipelineId, String propertyKey, String propertyValue, Optional<Boolean> unique) {
//        if (unique.isPresent() && unique.get()) {
//            clearProperty(propertyKey);
//        }
        var propertyDAO = new PipelineDefinitionPropertyDAO();
        propertyDAO.setValue(propertyValue);
        propertyDAO.setKey(propertyKey);

        return this.setProperty(pipelineId, propertyDAO, unique.orElse(false), true);

    }

    @Transactional
    public PipelineDefinitionPropertyDAO setProperty(String pipelineId, PipelineDefinitionPropertyDAO propertyDAO, Optional<Boolean> unique) {
//        if (unique.isPresent() && unique.get()) {
//            clearProperty(propertyDAO.getKey());
//        }
        return this.setProperty(pipelineId, propertyDAO, unique.orElse(false), false);
    }

    @Transactional
    private PipelineDefinitionPropertyDAO setProperty(String pipelineId, PipelineDefinitionPropertyDAO propertyDAO, Boolean unique, Boolean update) {
        Optional<PipelineDefinition> byId = pipelineRepository.findById(pipelineId);
        PipelineDefinition pd;
        if (byId.isPresent()) {
            pd = byId.get();
            if (unique) {
                try {
                    if (runlock.tryLock(15000L, TimeUnit.MILLISECONDS)) {
                        try {
                            this.assertRunning(pd);
                            clearProperty(propertyDAO.getKey());
                            return this.setProperty(pd, propertyDAO, update);
                        } finally {
                            runlock.unlock();
                        }
                    } else {
                        throw new RuntimeException("Pipeline is blocked");
                    }
                } catch (InterruptedException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return this.setProperty(pd, propertyDAO, update);
            }

        } else {
            pd = initPipelineDefinition(pipelineId);
            pipelineRepository.save(pd);
            return this.setProperty(pd, propertyDAO, update);
        }


    }


    @Transactional
    private PipelineDefinitionPropertyDAO setProperty(PipelineDefinition pipeline, PipelineDefinitionPropertyDAO propertyDAO, Boolean update) {


        PipelineDefinitionProperty property;
        var pipelineId = pipeline.getPipelineId();
        if (!update) {
            property = propertyDAO.mapToEntity();
            var p = pipelinePropertyRepository.getPipelineProperty(pipelineId, propertyDAO.getKey());
            if (p.isPresent()) {
                property.setId(p.get().getId());
            } else property.setId(null);
        } else {
            property = pipelinePropertyRepository.getPipelineProperty(pipelineId, propertyDAO.getKey())
                    .orElseThrow(() -> new NotFoundException(
                            "Pipeline property (" + pipelineId + "," + propertyDAO.getKey() + ") not exists  "));
            property.setValue(propertyDAO.getValue());


        }
        property.setPipelineDefinition(pipeline);

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

    public List<PipelineDefinitionDAO> getByProperty(String propertyKey, String propertyValue, Boolean visible) {
        var s = pipelineRepository.findByProperty(propertyKey, propertyValue, visible).stream();
        return s.map((it) -> {
            try {
                var kbfPipeline = kubeflowService.getPipeline(it.getPipelineId());
                if (!kbfPipeline.getName().equals(it.getName())) {
                    it.setName(kbfPipeline.getName());
                    pipelineRepository.save(it);
                }
                return this.enrichDAO(kbfPipeline, it, true);

            } catch (ParseException e) {
                log.error(
                        "Invalid pipeline: " + it.getPipelineId());
                return null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).filter(Objects::nonNull).toList();

    }

    //#endregion

}