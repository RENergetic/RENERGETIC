package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.MeasurementDAORequest;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.ResourceDAO;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.MeasurementTagsRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.common.repository.UuidRepository;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.dao.MeasurementDAOImpl;
import com.renergetic.hdrapi.dao.ResourceDAOImpl;
import com.renergetic.hdrapi.dao.details.MeasurementTagsDAO;
import com.renergetic.hdrapi.dao.details.TagDAO;
import com.renergetic.hdrapi.dao.temp.HDRRecommendation;
import com.renergetic.hdrapi.dao.temp.HDRRecommendationDAO;
import com.renergetic.hdrapi.dao.temp.RecommendationRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HDRRecommendationService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    MeasurementRepositoryTemp measurementRepository2;
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

    private Boolean save(LocalDateTime timestamp, List<HDRRecommendationDAO> recommendations) {
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

    public List<MeasurementDAOResponse> getMeasurements(HDRRecommendationDAO r) {
        recommendationRepository.findByTimestampTag(r.getTimestamp(), r.getTag().getTagId()).orElseThrow(
                () -> new NotFoundException(
                        "HDRRecommendation at: " + r.getTimestamp() + " and tag id: " + r.getTag().getTagId() + "not exists"));
        ;
        var tagId = r.getTag().getTagId();
        MeasurementTags tag = measurementTagsRepository.findById(tagId).orElseThrow(
                () -> new NotFoundException("Tag with id: " + tagId + " not exists"));
        List<Measurement> measurements = measurementTagsRepository.getMeasurementByTagId(tag.getId(), 0L, 100L);
        return measurements.stream().map(it -> MeasurementDAOResponse.create(it, null, null)).collect(
                Collectors.toList());

    }


}
