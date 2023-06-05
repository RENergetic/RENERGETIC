package com.renergetic.kpiapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;

public interface AbstractMeterRepository extends JpaRepository<AbstractMeterConfig, Long> {

	Optional<AbstractMeterConfig> findByNameAndDomain(AbstractMeter name, Domain domain);

	boolean existsByNameAndDomain(AbstractMeter name, Domain domain);

}
