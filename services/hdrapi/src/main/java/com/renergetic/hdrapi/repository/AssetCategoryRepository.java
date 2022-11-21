package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    List<AssetCategory> findByNameContaining(String term);
}
