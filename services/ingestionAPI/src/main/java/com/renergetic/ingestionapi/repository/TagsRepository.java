package com.renergetic.ingestionapi.repository;

import com.renergetic.ingestionapi.model.Tags;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tags, Long> {
}
