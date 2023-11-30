//package com.renergetic.hdrapi.dao.temp;
//
//import com.renergetic.common.model.HDRRecommendation;
//import com.renergetic.common.model.HDRRequest;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@SuppressWarnings("unchecked")
//public interface HDRRequestRepositoryTemp extends JpaRepository<HDRRequest, Long> {
//    @Query(value = "SELECT  max(\"timestamp\") as \"timestamp\" from hdr_request", nativeQuery = true)
//    Optional<LocalDateTime> getRecentRequestTimestamp();
//}
