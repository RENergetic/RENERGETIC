package com.renergetic.kubeflowapi.tempcommon;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, String> {
    PipelineRun save(PipelineRun pipelineRun);

}
