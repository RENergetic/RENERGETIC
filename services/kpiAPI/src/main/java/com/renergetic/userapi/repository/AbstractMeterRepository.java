package com.renergetic.userapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.userapi.model.AbstractMeterConfig;
import com.renergetic.userapi.model.Domain;

public interface AbstractMeterRepository extends JpaRepository<AbstractMeterConfig, Long> {

	Optional<AbstractMeterConfig> findByNameAndDomain(String name, Domain domain);

	boolean existsByNameAndDomain(String name, Domain domain);

}
