package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.Tags;
import com.renergetic.ingestionapi.model.TagsData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagsRepository extends JpaRepository<Tags, Long> {

    List<Tags> findByMeasurementIdIsNull();

    @Query(value = "SELECT DISTINCT t.key, t.value FROM tags t", nativeQuery = true)
    List<TagsData> findDistinct();
}