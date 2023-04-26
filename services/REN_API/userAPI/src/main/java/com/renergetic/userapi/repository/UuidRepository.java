package com.renergetic.userapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.userapi.model.UUID;


public interface UuidRepository extends JpaRepository<UUID, String>{
}
