package com.renergetic.hdrapi.dao.tempcommon;

import com.renergetic.common.dao.MeasurementDAO;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@SuppressWarnings("unchecked")
public interface TempMeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query(value = "SELECT distinct measurement.* " +
            "FROM (measurement " +
            "INNER INNER JOIN asset asset_conn ON  measurement.asset_id = asset_conn.id " +
            "INNER JOIN measurement_type ON measurement_type.id = measurement.measurement_type_id " +
            "LEFT JOIN measurement_details ON measurement_details.measurement_id = measurement.id " +
            ")" +
            " WHERE  measurement.asset_id = :assetId " +
            " AND  measurement_details.key =:mKey AND measurement_details.value =:mValue  ", nativeQuery = true)
    public List<Measurement> findByAssetIdAndDetails(Long assetId, @Param("mKey") String key,
                                                     @Param("mValue") String value);



}
