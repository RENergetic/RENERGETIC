package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.HDRMeasurementDAO;
import com.renergetic.common.dao.HDRRecommendationDAO;
import com.renergetic.common.dao.HDRRequestDAO;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.*;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.hdrapi.config.MeasurementRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HDRRecommendationService {
    @Autowired
    MeasurementRepository measurementRepository;
//    @Autowired
//    MeasurementRepository2 measurementRepository2;

    @Autowired
    HDRRecommendationRepository recommendationRepository;
    @Autowired
    HDRMeasurementRepository hdrMeasurementRepository;
    @Autowired
    private MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    HDRRequestRepository hdrRequestRepository;
    @Autowired
    UuidRepository uuidRepository;

    // ASSET CRUD OPERATIONS
    private HDRRecommendationDAO save(HDRRecommendationDAO recommendationDAO) {
        var r = recommendationRepository.findByTimestampTag(
                recommendationDAO.getTimestamp(),
                recommendationDAO.getTag().getId());
        if (r.isPresent()) {
            var recommendation = r.get();
            recommendation.setLabel(recommendationDAO.getLabel());
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendation));
        } else {
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendationDAO.mapToEntity()));
        }

    }

    public HDRRequestDAO save(HDRRequestDAO requestDAO) {
        var r = hdrRequestRepository.findRequestByTimestamp(requestDAO.getTimestamp());
        if (r.isPresent()) {
            throw new InvalidArgumentException("Request exists");
        }
        var recentRequestTimestamp = hdrRequestRepository.getRecentRequestTimestamp();
        if (recentRequestTimestamp.isPresent() &&
                requestDAO.getTimestamp() < recentRequestTimestamp.get()) {
            throw new InvalidArgumentException("Request is outdated");
        } else {
            var request = hdrRequestRepository.save(requestDAO.mapToEntity());
            return HDRRequestDAO.create(request);
        }

    }

    public boolean append(long t, List<HDRRecommendationDAO> recommendations) {
        if (recommendationRepository.timestampExists(t).isPresent())
            return this.save(t, recommendations);
        else
            throw  new InvalidArgumentException("Timestamp does not exist");
    }

    public Boolean save(long t, List<HDRRecommendationDAO> recommendations) {

        recommendations.forEach(it -> it.setTimestamp(it.getTimestamp() != null ? it.getTimestamp() : t));
        var differentTimestamp = recommendations.stream()
                .filter(it -> it.getTimestamp() != t).findAny();
        if (differentTimestamp.isPresent()) {
            throw new InvalidArgumentException("Recommendation has different timestamp");
        }
        recommendations.forEach(it -> {
            if (it.getTag().getId() == null) {
                var id = measurementTagsRepository.findByKeyAndValue(it.getTag().getKey(), it.getTag().getValue()).get(0).getId();
                it.getTag().setId(id);
            }
        });
        try {

            recommendations.forEach(this::save);
        } catch (Exception ex) {
            deleteByTimestamp(t);
            // todo: more logging details?
            return false;
        }
        return true;

    }

    public void deleteByTimestamp(Long t) {
        recommendationRepository.deleteByTimestamp(t);
    }

    public void deleteRequestByTimestamp(Long t) {
        hdrRequestRepository.deleteByTimestamp(t);
    }


    public List<Long> list(Long t) {
        return recommendationRepository.listRecentRecommendations(t);
    }

    public Optional<List<HDRRecommendationDAO>> getRecent() {
        var t = recommendationRepository.getRecentRecommendation();

        List<HDRRecommendationDAO> r = null;
        if (t.isPresent()) {
            var requestTimestamp = hdrRequestRepository.getRecentRequestTimestamp();
            if (requestTimestamp.isPresent() && requestTimestamp.get() > t.get()) {
//                throw new InvalidArgumentException("recommendations are out outdated");
                return Optional.ofNullable(r);
            }
            r = this.getRecommendations(t.get());
        }
        return Optional.ofNullable(r);
    }

    public List<HDRRequestDAO> getRecentRequest() {
        var t = hdrRequestRepository.getRecentRequestTimestamp();//get timestamp of the last request batch
        if (t.isPresent()) {
            var now = DateConverter.toEpoch(LocalDateTime.now());
            //get requests which haven't ended yet
            return this.getRequests(t.get()).stream().filter(it -> it.getDateTo() > now).collect(Collectors.toList());

        }


        return Collections.emptyList();
    }

    public List<HDRRecommendationDAO> getRecommendations(Long t) {
        if (t == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        return recommendationRepository.findByTimestamp(t).stream().map(HDRRecommendationDAO::create).collect(
                Collectors.toList());
    }

    public List<MeasurementDAOResponse> getMeasurements(Long timestamp) {
        if (timestamp == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
//        var t = DateConverter.toLocalDateTime(timestamp);
        return hdrMeasurementRepository.listMeasurement(timestamp).stream()
                .map(it -> MeasurementDAOResponse.create(it.getMeasurement(), null, null))
                .collect(Collectors.toList());
    }

    public List<MeasurementDAOResponse> getMeasurements(Long timestamp, String key, String value) {
        if (timestamp == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }

//        var t = DateConverter.toLocalDateTime(timestamp);
        return measurementRepository.listHDRMeasurement(timestamp, key, value).stream()
                .map(it -> MeasurementDAOResponse.create(it, null, null))
                .collect(Collectors.toList());
    }

    public HDRMeasurementDAO setMeasurement(Long timestamp, Long measurementId) {
        if (timestamp == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
//        var t = DateConverter.toLocalDateTime(timestamp);
//        hdrRequestRepository.findRequestByTimestamp(t);
        HDRMeasurement hdrMeasurement = hdrMeasurementRepository.findByIdAndTimestamp(measurementId,
                timestamp).orElse(null);

        if (hdrMeasurement == null) {
            var measurement = measurementRepository.getById(measurementId);
            hdrMeasurement = new HDRMeasurement();
            hdrMeasurement.setMeasurement(measurement);
            hdrMeasurement.setTimestamp(timestamp);
            hdrMeasurementRepository.save(hdrMeasurement);
        }
        return HDRMeasurementDAO.create(hdrMeasurement);


    }

    @Transactional
    public List<HDRMeasurementDAO> setMeasurements(Long timestamp, List<Long> measurementIds) {
        if (timestamp == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        var t = DateConverter.toLocalDateTime(timestamp);
//        hdrRequestRepository.findRequestByTimestamp(t);

        var l = measurementIds.stream().map(id -> {
                    var measurement = measurementRepository.getById(id);
                    var hdrMeasurement = new HDRMeasurement();
                    hdrMeasurement.setMeasurement(measurement);
                    hdrMeasurement.setTimestamp(timestamp);
                    return hdrMeasurement;
                }
        ).map(it -> hdrMeasurementRepository.save(it)).map(HDRMeasurementDAO::create).collect(
                Collectors.toList());
        return l;
    }

    public HDRMeasurementDAO deleteMeasurement(Long measurementId, Long timestamp) {
        if (timestamp == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        var t = DateConverter.toLocalDateTime(timestamp);
//        hdrRequestRepository.findRequestByTimestamp(t);
        Optional<HDRMeasurement> byIdAndTimestamp = hdrMeasurementRepository.findByIdAndTimestamp(measurementId, timestamp);
        if (byIdAndTimestamp.isEmpty()) {
            throw new NotFoundException("Maasurement: " + measurementId + " , at " + t + " -does not exist");
        }
        HDRMeasurement hdrMeasurement = byIdAndTimestamp.get();
        var dao = HDRMeasurementDAO.create(hdrMeasurement);
        hdrMeasurementRepository.delete(hdrMeasurement);
        return dao;
    }

//    @Transactional
//    public List<HDRMeasurementDAO> setMeasurements(LocalDateTime t, List<HDRMeasurementDAO> measurements) {
//        if (t == null) {
//            throw new InvalidArgumentException("Empty timestamp");
//        }
////        hdrRequestRepository.findRequestByTimestamp(t);
//
//        var l = measurements.stream().map(HDRMeasurementDAO::mapToEntity)
//                .map(it -> tempHDRMeasurementRepository.save(it)).map(HDRMeasurementDAO::create).collect(
//                        Collectors.toList());
//        return l;
//    }

    public List<HDRRequestDAO> getRequests(Long t) {
        if (t == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        return hdrRequestRepository.findRequestByTimestamp(t).stream().map(HDRRequestDAO::create).collect(
                Collectors.toList());
    }

//    public List<MeasurementDAOResponse> getMeasurements(HDRRecommendation r) {
//        recommendationRepository.findByTimestampTag(r.getTimestamp(), r.getTag().getId()).orElseThrow(
//                () -> new NotFoundException(
//                        "HDRRecommendation at: " + r.getTimestamp() + " and tag id: " + r.getTag().getId() + "not exists"));
//        ;
//        var tagId = r.getTag().getId();
//        MeasurementTags tag = measurementTagsRepository.findById(tagId).orElseThrow(
//                () -> new NotFoundException("Tag with id: " + tagId + " not exists"));
//        List<Measurement> measurements = measurementRepository.getMeasurementByTagId(tag.getId(), 0L, 100L);
//        return measurements.stream().map(it -> MeasurementDAOResponse.create(it, null, null)).collect(
//                Collectors.toList());
//
//    }

//    public List<MeasurementDAOResponse> getMeasurements(Long HDRRecommendationId) {
//        var r = recommendationRepository.findById(HDRRecommendationId).orElseThrow(() -> new NotFoundException(
//                "HDRRecommendationId with an id " + HDRRecommendationId + "does not exists"));
//        return this.getMeasurements(r);
//
//    }


}
