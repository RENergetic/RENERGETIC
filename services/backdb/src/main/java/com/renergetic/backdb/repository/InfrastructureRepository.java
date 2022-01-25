package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Infrastructure;

@SuppressWarnings("unchecked")
public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {
	List<Infrastructure> findByName (String name);
	
	Infrastructure save(Infrastructure infrastructure);

	@Transactional
	@Modifying
	@Query("update Infrastructure i set i.name = :#{#iparam.name}, i.description = :#{#iparam.description}, i.type = :#{#iparam.type}, i.operatorUserId = :#{#iparam.operatorUserId}, i.energyStored = :#{#iparam.energyStored} where i.id = :id")
	int update(@Param("iparam") Infrastructure infrastructure, Long id);

	List<Infrastructure> findByType(String type);
}
