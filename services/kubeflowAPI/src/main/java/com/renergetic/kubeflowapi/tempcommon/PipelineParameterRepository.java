package com.renergetic.kubeflowapi.tempcommon;


import com.renergetic.kubeflowapi.tempcommon.PipelineParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineParameterRepository extends JpaRepository<PipelineParameter, String> {


}
