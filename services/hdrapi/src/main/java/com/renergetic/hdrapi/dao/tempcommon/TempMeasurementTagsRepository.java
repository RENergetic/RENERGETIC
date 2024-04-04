package com.renergetic.hdrapi.dao.tempcommon;

import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


@SuppressWarnings("unchecked")
public interface TempMeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {


    @Modifying
    @Transactional
    @Query(value = " DELETE   from measurement_tags using tags " +
            " WHERE tags.id = measurement_tags.tag_id  and" +
            " measurement_id =  :measurement_id and tags.key = :tagKey  ", nativeQuery = true)
    public int clearTag(@Param("measurement_id") Long measurementId, @Param("tagKey") String tagKey);

//    @Modifying
//    @Transactional
//    @Query(value = " DELETE   from measurement_tags using tags " +
//            " WHERE tags.id = measurement_tags.tag_id  and" +
//            " measurement_id =  :measurement_id and tags.key = :tagKey  ", nativeQuery = true)
//    public int clearTag(@Param("measurement_id") Long measurementId, @Param("tagKey") String tagKey);
}
