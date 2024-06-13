//package com.renergetic.hdrapi.dao.tempcommon;
//
//import com.renergetic.common.model.details.MeasurementTags;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import javax.transaction.Transactional;
//import java.util.List;
//
//
//@SuppressWarnings("unchecked")
//public interface TempMeasurementTagsRepository extends JpaRepository<MeasurementTags, Long> {
//
//
//    @Query(value = "SELECT mt.* " +
//            " FROM tags mt " +
//            " WHERE mt.key=:key   ", nativeQuery = true)
//    List<MeasurementTags> findByKey(@Param("key") String key);
//
//    @Query(value = "SELECT distinct tags.value  from tags WHERE tags.key = :tagKey", nativeQuery = true)
//    List<String> listTagValues(String tagKey);
//
//}
