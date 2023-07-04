package com.renergetic.common.repository;

import com.renergetic.common.model.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    List<AssetCategory> findByNameContaining(String term);
}
