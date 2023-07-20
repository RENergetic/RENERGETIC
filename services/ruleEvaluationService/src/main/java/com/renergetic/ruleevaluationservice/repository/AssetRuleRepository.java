package com.renergetic.ruleevaluationservice.repository;

import com.renergetic.ruleevaluationservice.model.AssetRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRuleRepository extends JpaRepository<AssetRule, Long> {
    List<AssetRule> findByAssetId(Long id);
    List<AssetRule> findByActiveTrue();
}
