package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.UserSettings;

@SuppressWarnings("unchecked")
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
	UserSettings save(UserSettings role);
	
	List<UserSettings> findByUserId(Long userId);
}
