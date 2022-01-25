package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.DemandInformation;

@SuppressWarnings("unchecked")
public interface DemandInformationRepository extends JpaRepository<DemandInformation, Long> {
	DemandInformation save(DemandInformation information);

	List<DemandInformation> findByName (String name);
	List<DemandInformation> findByDemandId(long resource_id);
	DemandInformation findByIdAndDemandId(long id, long resource_id);
}
