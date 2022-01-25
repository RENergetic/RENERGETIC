package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.InfrastructureInformation;

@SuppressWarnings("unchecked")
public interface InfrastructureInformationRepository extends JpaRepository<InfrastructureInformation, Long> {	
	InfrastructureInformation save(InfrastructureInformation information);

	List<InfrastructureInformation> findByName (String name);
	List<InfrastructureInformation> findByInfrastructureId(long resource_id);
	InfrastructureInformation findByIdAndInfrastructureId(long id, long resource_id);
}
