package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.HDRRequest;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface WorkFlowRepository extends JpaRepository<WorkFlowDefinition, Long> {


    HDRRequest save(WorkFlowDefinition request);


}
