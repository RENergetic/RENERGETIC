package com.renergetic.backdb.repository.information;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.information.UserInformation;

@SuppressWarnings("unchecked")
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
	UserInformation save(UserInformation information);

	List<UserInformation> findByName (String name);
	List<UserInformation> findByUserId(long resource_id);
	UserInformation findByIdAndUserId(long id, long resource_id);
}
