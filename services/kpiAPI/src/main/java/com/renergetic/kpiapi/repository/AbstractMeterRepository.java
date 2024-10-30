package com.renergetic.kpiapi.repository;

import java.util.List;
import java.util.Optional;

import com.renergetic.common.model.Domain;
import com.renergetic.kpiapi.dao.AbstractMeterIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface AbstractMeterRepository extends JpaRepository<AbstractMeterConfig, Long> {

	Optional<AbstractMeterConfig> findByNameAndDomain(AbstractMeter name, Domain domain);

	boolean existsByNameAndDomain(AbstractMeter name, Domain domain);

	@Transactional
	void deleteByNameAndDomain(AbstractMeter name, Domain domain);

	//	@Query(value = "SELECT meters.name FROM " +
//			" values('LRS'),('LNS'),('ERS'),('ENS'),('LOSSES'), " +
//			" ('LOAD'),('EXCESS'),('STORAGE'),('RES'),('NONRES')) as meters(\"name\") " +
//			" LEFT JOIN " +
//			" abstract_meter  on abstract_meter.name = meters.name " +
//			" group by meters.name HAVING count(*) <3 " +  , nativeQuery = true)
//	public List<AbstractMeterIdentifier> listNotConfiguredMeters( );
	@Query(value = "SELECT meters.name as \"name\",domains.domain as \"domain\", null as customName " +
			" FROM " +
			" (VALUES('LRS'),('LNS'),('ERS'),('ENS'),('LOSSES'), " +
			" ('LOAD'),('EXCESS'),('STORAGE'),('RES'),('NONRES')) as meters(\"name\") " +
			" JOIN (VALUES('heat'),('electricity'),('none')) AS domains(\"domain\") on TRUE " +
			" LEFT JOIN abstract_meter on abstract_meter.name = meters.name and abstract_meter.domain = domains.domain" +
			" WHERE abstract_meter.name is NULL or abstract_meter.measurement_id is NULL ", nativeQuery = true)
	public List<AbstractMeterIdentifier> listNotConfiguredMeters();

}