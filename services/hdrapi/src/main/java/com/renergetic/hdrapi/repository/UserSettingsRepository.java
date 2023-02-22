package com.renergetic.hdrapi.repository;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@SuppressWarnings("unchecked")
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    UserSettings save(UserSettings role);

    List<UserSettings> findByUserId(Long userId);
    @Modifying
    @Query("Delete  FROM UserSettings userSettings WHERE userSettings.user.id = :userId ")
    Long deleteByUserId(@Param("userId") Long userId );




}
