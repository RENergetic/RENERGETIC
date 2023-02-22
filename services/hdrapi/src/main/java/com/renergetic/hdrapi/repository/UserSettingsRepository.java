package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings("unchecked")
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    UserSettings save(UserSettings role);

    List<UserSettings> findByUserId(Long userId);
    @Modifying
    @Query(value = "Delete FROM   user_settings WHERE user_settings.user_id = :userId ",nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId );




}
