package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.HDRRecommendationDAO;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HDRRecommendationService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    MeasurementTypeRepository measurementTypeRepository;
    @Autowired
    MeasurementDetailsRepository measurementDetailsRepository;
    @Autowired
    MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    RecommendationRepository recommendationRepository;
    @Autowired
    UuidRepository uuidRepository;

    // ASSET CRUD OPERATIONS
    private HDRRecommendationDAO save(HDRRecommendationDAO recommendationDAO) {
        var r = recommendationRepository.findByTimestampTag(recommendationDAO.getTimestamp(),
                recommendationDAO.getTag().getTagId());
        if (r.isPresent()) {
            var recommendation = r.get();
            recommendation.setLabel(recommendationDAO.getLabel());
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendation));
        } else {
            return HDRRecommendationDAO.create(recommendationRepository.save(recommendationDAO.mapToEntity()));
        }

    }

    public Boolean save(LocalDateTime timestamp, List<HDRRecommendationDAO> recommendations) {
        var differentTimestamp = recommendations.stream().filter(it -> it.getTimestamp() != timestamp).findAny();
        if (differentTimestamp.isPresent()) {
            throw new InvalidArgumentException("Recommendation has different timestamp");
        }
        try {
            recommendations.forEach(this::save);
        } catch (Exception ex) {
            deleteByTimestamp(timestamp);
            // todo: more logging details?
            return false;
        }
        return true;

    }

    public void deleteByTimestamp(LocalDateTime t) {
        recommendationRepository.deleteByTimestamp(t);
    }


    public List<LocalDateTime> list(LocalDateTime t) {
        return recommendationRepository.listRecentRecommendations(t);
    }

    public Optional<List<HDRRecommendationDAO>> getRecent() {
        var t = recommendationRepository.getRecentRecommendation();
        List<HDRRecommendationDAO> r = null;
        if (t.isPresent())
            r = this.get(t.get());
        return Optional.ofNullable(r);
    }

    public List<HDRRecommendationDAO> get(LocalDateTime t) {
        if (t == null) {
            throw new InvalidArgumentException("Empty timestamp");
        }
        return recommendationRepository.findByTimestamp(t).stream().map(HDRRecommendationDAO::create).collect(
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
        List<Measurement> measurements = measurementTagsRepository.getMeasurementByTagId(tag.getId(), 0L, 100L);
        return measurements.stream().map(it -> MeasurementDAOResponse.create(it, null, null)).collect(
                Collectors.toList());

    }

    public List<MeasurementDAOResponse> getMeasurements(Long HDRRecommendationId) {
        var r = recommendationRepository.findById(HDRRecommendationId).orElseThrow(() -> new NotFoundException(
                "HDRRecommendationId with an id " + HDRRecommendationId + "does not exists"));
        return this.getMeasurements(r);

    }


}
