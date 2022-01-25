package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.SupplyInformation;

@SuppressWarnings("unchecked")
public interface SupplyInformationRepository extends JpaRepository<SupplyInformation, Long> {
	SupplyInformation save(SupplyInformation information);

	List<SupplyInformation> findByName (String name);
	List<SupplyInformation> findBySupplyId(long resource_id);
	SupplyInformation findByIdAndSupplyId(long id, long resource_id);
}
