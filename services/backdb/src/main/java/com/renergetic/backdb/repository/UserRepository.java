package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.User;

@SuppressWarnings("unchecked")
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByName (String name);
	
	User save(User user);

	@Transactional
	@Modifying
	@Query("update User u set u.name = :#{#user.name}, u.resideAsset.id = :#{#user.resideAsset.id} where u.id = :id")
	int update(@Param("user") User user, Long id);

	List<User> findByResideAssetId(String asset_id);
	
	@Query("select a from Asset a where a.id = (select u.resideAsset.id from User u where u.id = :id)")
	Asset findResidence(Long id);
}
