package com.renergetic.userapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.userapi.model.UserSettings;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long>{

	List<UserSettings> findByUserId(Long userId);

}
