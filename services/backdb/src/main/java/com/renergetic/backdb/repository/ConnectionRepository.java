package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Connection;

@SuppressWarnings("unchecked")
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
	List<Connection> findByName (String name);
	
	Connection save(Connection connection);

	@Transactional
	@Modifying
	@Query("update Connection c set c.name = :#{#connex.name}, c.description = :#{#connex.description}, c.asset.id = :#{#connex.asset.id}, c.inputInfrastructure.id = :#{#connex.inputInfrastructure.id}, c.outputInfrastructure.id = :#{#connex.outputInfrastructure.id}, c.inputPower = :#{#connex.inputPower}, c.outputPower = :#{#connex.outputPower} where c.id = :id")
	int update(@Param("connex") Connection connection, Long id);

	List<Connection> findByAssetId(String asset_id);
}
