package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.hdrapi.model.details.MeasurementTags;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {

    MeasurementTags save(MeasurementTags tag);

    @Query(value = "SELECT t.* " +
            "FROM (tags t " +
            "INNER JOIN measurement_tags connection ON connection.tag_id = t.id) " +
            "WHERE connection.measurement_id = :measurementId", nativeQuery = true)
    List<MeasurementTags> findByMeasurementId(Long measurementId);

    @Query(value = "SELECT mt " +
            "FROM tags" +
            "WHERE mt.value=key and mt.value =:value ")
    List<MeasurementTags> findByKeyAndValue(String key, String value);


    @Modifying
    @Transactional
    @Query(value = " INSERT INTO measurement_tags ( tag_id, measurement_id) VALUES " +
            " (:tag_id,:measurement_id  )  ON CONFLICT (tag_id,measurement_id) " +
            "   DO UPDATE SET tag_id =  :tag_id ; ", nativeQuery = true)
    public int setTag(@Param("tag_id") Long tagId, @Param("measurement_id") Long measurementId);

    @Modifying
    @Transactional
    @Query(value = " DELETE from measurement_tags WHERE measurement_id = :measurement_id ", nativeQuery = true)
    public int clearTags(@Param("measurement_id") Long measurementId);

}
