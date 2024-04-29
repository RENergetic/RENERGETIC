package com.renergetic.common.repository;


import com.renergetic.common.model.PipelineRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, String> {
    PipelineRun save(PipelineRun pipelineRun);

}
