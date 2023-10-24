package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface MeasurementRepositoryTemp  extends JpaRepository<Measurement, Long> {
//TODO: move to MeasurementRepository
    @Query(
            value = "SELECT _m.* FROM  easurement _m " +
                    " WHERE  COALESCE(_m.name like CONCAT('%', :name, '%'),:name is null ) " +
                    " AND COALESCE(_m.label like CONCAT('%', :label,'%'),TRUE )  " +
                    " LIMIT :limit OFFSET :offset ;",
            nativeQuery = true
    )
    List<Measurement> filterMeasurement(@Param("name") String name, @Param("label") String label,     @Param("offset") long offset, @Param("limit") int limit);

}
