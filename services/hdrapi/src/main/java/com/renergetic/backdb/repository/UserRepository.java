package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.User;

@SuppressWarnings("unchecked")
public interface UserRepository extends JpaRepository<User, Long> {
	User save(User user);
	
}
