package com.renergetic.common.repository;


import com.renergetic.common.model.PipelineDefinition;
import com.renergetic.common.model.PipelineRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, String> {
    PipelineRun save(PipelineRun pipelineRun);

    @Query(value = "SELECT pipeline_run.* FROM pipeline_run pipeline_run" +
            " WHERE pipeline_run.pipeline_id = :pipelineId" +
            " AND COALESCE(pipeline_run.start_time > :from,TRUE) " +
            " AND COALESCE(pipeline_run.start_time < :to,TRUE) " +
            " order by pipeline_run.init_time DESC " +
            " LIMIT :limit OFFSET :offset ;", nativeQuery = true)

    public List<PipelineRun> getPipelineRuns(String pipelineId, Long from, Long to, Long offset, Long limit);
}
