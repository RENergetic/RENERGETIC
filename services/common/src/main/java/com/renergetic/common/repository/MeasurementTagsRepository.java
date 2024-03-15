package com.renergetic.common.repository;

import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

import jakarta.transaction.Transactional;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {
	
	MeasurementTags save(MeasurementTags tag);
	
	@Query(value = "SELECT t.* " +
			"FROM (tags t " +
			"INNER JOIN measurement_tags connection ON connection.tag_id = t.id) " +
			"WHERE connection.measurement_id = :measurementId", nativeQuery = true)
	List<MeasurementTags> findByMeasurementId(Long measurementId);



    @Query(value = "SELECT mt.* " +
            " FROM tags mt " +
            " WHERE mt.key=:key and mt.value =:value ", nativeQuery = true)
    List<MeasurementTags> findByKeyAndValue(@Param("key") String key, @Param("value") String value);
    @Query(value = "SELECT distinct key  from tags;", nativeQuery = true)
    List<String> listTagKeys( );

    @Query(value = "SELECT distinct key  from tags WHERE tags.key = :tagKey", nativeQuery = true)
    List<String> listTagValues(String tagKey);

    @Modifying
    @Transactional
    @Query(value = " INSERT INTO measurement_tags ( tag_id, measurement_id) VALUES " +
            " (:tag_id,:measurement_id  ) ", nativeQuery = true)
    public int setTag(@Param("tag_id") Long tagId, @Param("measurement_id") Long measurementId);

    @Modifying
    @Transactional
    @Query(value = " DELETE from measurement_tags WHERE measurement_id = :measurement_id ", nativeQuery = true)
    public int clearTags(@Param("measurement_id") Long measurementId);

}
