package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.AssetCategory;
import com.renergetic.hdrapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    List<AssetCategory> findByNameContaining(String term);
}
