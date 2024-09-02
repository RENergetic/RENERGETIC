package com.renergetic.common.repository;

import com.renergetic.common.model.Rule;
import com.renergetic.common.model.RuleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    boolean existsByIdAndRootTrue(Long id);
    List<Rule> findByActiveTrueAndRootTrue();
    List<Rule> findByRootTrue();
}
