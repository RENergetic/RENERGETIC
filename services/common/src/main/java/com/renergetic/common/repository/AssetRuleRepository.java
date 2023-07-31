package com.renergetic.common.repository;

import com.renergetic.common.model.AssetRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRuleRepository extends JpaRepository<AssetRule, Long> {
    List<AssetRule> findByAssetId(Long id);
    List<AssetRule> findByActiveTrue();
}
