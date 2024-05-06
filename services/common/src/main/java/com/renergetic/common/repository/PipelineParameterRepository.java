package com.renergetic.common.repository;


import com.renergetic.common.model.PipelineParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineParameterRepository extends JpaRepository<PipelineParameter, String> {


}
