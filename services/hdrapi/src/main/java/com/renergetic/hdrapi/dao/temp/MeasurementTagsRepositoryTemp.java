package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.details.MeasurementTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementTagsRepositoryTemp extends JpaRepository<MeasurementTags, Long> {


    @Query(value = "SELECT distinct key  from tags;", nativeQuery = true)
    List<String> listTagKeys();

    @Query(value = "SELECT distinct key  from tags WHERE tags.key = :tagKey", nativeQuery = true)
    List<String> listTagValues(String tagKey);

}
