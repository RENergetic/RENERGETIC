package com.renergetic.common.repository;

import com.renergetic.common.model.OptimizerParameter;
import com.renergetic.common.model.OptimizerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptimizerParameterRepository extends JpaRepository<OptimizerParameter, Long> {
}
