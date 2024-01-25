package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.HDRRecommendationDAO;
import com.renergetic.common.dao.HDRRequestDAO;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.hdrapi.dao.temp.RecommendationRepositoryTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HDRRecommendationService {
    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    RecommendationRepository recommendationRepository;
    @Autowired
    RecommendationRepositoryTemp recommendationRepositoryTemp;
    @Autowired
    HDRRequestRepository hdrRequestRepository;
    @Autowired
    UuidRepository uuidRepository;

    // ASSET CRUD OPERATIONS
    private HDRRecommendationDAO save(HDRRecommendationDAO recommendationDAO) {
        var r = recommendationRepositoryTemp.findByTimestampTag(
                DateConverter.toLocalDateTime(recommendationDAO.getTimestamp()),
                recommendationDAO.getTag().getTagId());
        if (r.isPresent()) {
            var recommendation = r.get();
            recommendation.setLabel(recommendationDAO.getLabel());
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendation));
        } else {
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendationDAO.mapToEntity()));
        }

    }

    public HDRRequestDAO save(HDRRequestDAO requestDAO) {
        var r = hdrRequestRepository.findRequestByTimestamp(DateConverter.toLocalDateTime(requestDAO.getTimestamp()));
        if (r.isPresent()) {
            throw new InvalidArgumentException("Request exists");
        }
        var recentRequestTimestamp = hdrRequestRepository.getRecentRequestTimestamp();
        if (recentRequestTimestamp.isPresent() &&
                DateConverter.toLocalDateTime(requestDAO.getTimestamp())
                        .isBefore(recentRequestTimestamp.get())) {
            throw new InvalidArgumentException("Request is outdated");
        } else {
            var request = hdrRequestRepository.save(requestDAO.mapToEntity());
            return HDRRequestDAO.create(request);
        }

    }

    public Boolean save(long timestamp, List<HDRRecommendationDAO> recommendations) {
        recommendations.forEach(it -> it.setTimestamp(it.getTimestamp() != null ? it.getTimestamp() : timestamp));
        var differentTimestamp = recommendations.stream()
                .filter(it -> it.getTimestamp() != timestamp).findAny();
        if (differentTimestamp.isPresent()) {
            throw new InvalidArgumentException("Recommendation has different timestamp");
        }
        try {
            recommendations.forEach(this::save);
        } catch (Exception ex) {
            deleteByTimestamp(DateConverter.toLocalDateTime(timestamp));
            // todo: more logging details?
            return false;
        }
        return true;

    }

    public void deleteByTimestamp(LocalDateTime t) {
        recommendationRepository.deleteByTimestamp(t);
    }

    public void deleteRequestByTimestamp(LocalDateTime t) {
        hdrRequestRepository.deleteByTimestamp(t);
    }


    public List<LocalDateTime> list(LocalDateTime t) {
        return recommendationRepository.listRecentRecommendations(t);
    }

    public Optional<List<HDRRecommendationDAO>> getRecent() {
        var t = recommendationRepository.getRecentRecommendation();
        List<HDRRecommendationDAO> r = null;
        if (t.isPresent())
            r = this.getRecommendations(t.get());
        return Optional.ofNullable(r);
    }

    public List<HDRRequestDAO> getRecentRequest() {
        var t = hdrRequestRepository.getRecentRequestTimestamp();//get timestamp of the last request batch
        if (t.isPresent()) {
            var now = DateConverter.toEpoch(LocalDateTime.now());
            //get requests which haven't ended yet
            return this.getRequests(t.get()).stream().filter(it->it.getDateTo()>now).collect(Collectors.toList());

        }


        return Collections.emptyList();
    }

    public List<HDRRecommendationDAO> getRecommendations(LocalDateTime t) {
        if (t == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        return recommendationRepository.findByTimestamp(t).stream().map(HDRRecommendationDAO::create).collect(
                Collectors.toList());
    }

    public List<HDRRequestDAO> getRequests(LocalDateTime t) {
        if (t == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        return hdrRequestRepository.findRequestByTimestamp(t).stream().map(HDRRequestDAO::create).collect(
                Collectors.toList());
    }

    public List<MeasurementDAOResponse> getMeasurements(HDRRecommendation r) {
        recommendationRepository.findByTimestampTag(r.getTimestamp(), r.getTag().getId()).orElseThrow(
                () -> new NotFoundException(
                        "HDRRecommendation at: " + r.getTimestamp() + " and tag id: " + r.getTag().getId() + "not exists"));
        ;
        var tagId = r.getTag().getId();
        MeasurementTags tag = measurementTagsRepository.findById(tagId).orElseThrow(
                () -> new NotFoundException("Tag with id: " + tagId + " not exists"));
        List<Measurement> measurements = measurementRepository.getMeasurementByTagId(tag.getId(), 0L, 100L);
        return measurements.stream().map(it -> MeasurementDAOResponse.create(it, null, null)).collect(
                Collectors.toList());

    }

    public List<MeasurementDAOResponse> getMeasurements(Long HDRRecommendationId) {
        var r = recommendationRepository.findById(HDRRecommendationId).orElseThrow(() -> new NotFoundException(
                "HDRRecommendationId with an id " + HDRRecommendationId + "does not exists"));
        return this.getMeasurements(r);

    }


}
