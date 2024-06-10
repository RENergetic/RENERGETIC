package com.renergetic.common.repository;

import com.renergetic.common.model.RuleAction;
import com.renergetic.common.model.RuleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleActionRepository extends JpaRepository<RuleAction, Long> {
}
