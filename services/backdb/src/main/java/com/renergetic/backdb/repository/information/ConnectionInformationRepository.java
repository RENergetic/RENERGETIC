package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.ConnectionInformation;

@SuppressWarnings("unchecked")
public interface ConnectionInformationRepository extends JpaRepository<ConnectionInformation, Long> {	
	ConnectionInformation save(ConnectionInformation information);

	List<ConnectionInformation> findByName (String name);
	List<ConnectionInformation> findByConnectionId(long resource_id);
	ConnectionInformation findByIdAndConnectionId(long id, long resource_id);
}
