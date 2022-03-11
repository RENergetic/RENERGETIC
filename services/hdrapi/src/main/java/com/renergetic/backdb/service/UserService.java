package com.renergetic.backdb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.renergetic.backdb.dao.UserDAORequest;
import com.renergetic.backdb.dao.UserDAOResponse;
import com.renergetic.backdb.dao.UserRolesDAO;
import com.renergetic.backdb.dao.UserSettingsDAO;
import com.renergetic.backdb.model.User;
import com.renergetic.backdb.model.UserRoles;
import com.renergetic.backdb.model.UserSettings;
import com.renergetic.backdb.repository.UserRepository;
import com.renergetic.backdb.repository.UserRolesRepository;
import com.renergetic.backdb.repository.UserSettingsRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;

@Service
public class UserService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRolesRepository userRolesRepository;
	@Autowired
	UserSettingsRepository userSettingsRepository;

	// USER CRUD OPERATIONS
	public UserDAOResponse save(UserDAORequest user) {
		user.setId(null);
		return UserDAOResponse.create(userRepository.save(user.mapToEntity()), null, null);
	}

	public UserRolesDAO saveRole(UserRolesDAO role) {
		if ( userRepository.existsById(role.getUser_id()) ) {
			role.setId(null);
			return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
		} else return null;
	}

	public UserSettingsDAO saveSetting(UserSettingsDAO settings) {
		if ( userRepository.existsById(settings.getUser_id()) ) {
			settings.setId(null);
			return UserSettingsDAO.create(userSettingsRepository.save(settings.mapToEntity()));
		} else return null;
	}
	
	public boolean deleteById(Long id) {
		if (id != null && userRepository.existsById(id)) {
			userRepository.deleteById(id);
			return true;
		} else return false;
	}
	
	public boolean deleteRoleById(Long id) {
		if (id != null && userRolesRepository.existsById(id)) {
			userRolesRepository.deleteById(id);
			return true;
		} else return false;
	}
	
	public boolean deleteSettingById(Long id) {
		if (id != null && userSettingsRepository.existsById(id)) {
			userSettingsRepository.deleteById(id);
			return true;
		} else return false;
	}

	public UserDAOResponse update(UserDAORequest user, Long id) {
		if ( userRepository.existsById(id) ) {
			user.setId(id);
			return UserDAOResponse.create(userRepository.save(user.mapToEntity()), null, null);
		} else return null;
	}

	public UserRolesDAO updateRole(UserRolesDAO role, Long id) {
		if ( userRolesRepository.existsById(id) && userRepository.existsById(role.getUser_id()) ) {
			role.setId(id);
			return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
		} else return null;
	}

	public UserSettingsDAO updateSetting(UserSettingsDAO setting, Long id) {
		if ( userSettingsRepository.existsById(id) && userRepository.existsById(setting.getUser_id()) ) {
			setting.setId(id);
			return UserSettingsDAO.create(userSettingsRepository.save(setting.mapToEntity()));
		} else return null;
	}

	public List<UserDAOResponse> get(Map<String, String> filters, long offset, int limit) {
	Page<User> users = userRepository.findAll(new OffSetPaging(offset, limit));
		Stream<User> stream = users.stream();
		
		if (filters != null)
			stream.filter(user -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = user.getName().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("island_id"))
					equals = String.valueOf(user.getIsland().getId()).equalsIgnoreCase(filters.get("island_id"));
				
				return equals;
			});
		return stream
				.map(user -> UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()), userSettingsRepository.findByUserId(user.getId())))
				.collect(Collectors.toList());
	}

	public List<UserRolesDAO> getRoles(Map<String, String> filters, long offset, int limit) {
		Page<UserRoles> roles = userRolesRepository.findAll(new OffSetPaging(offset, limit));
		Stream<UserRoles> stream = roles.stream();

		if (filters != null)
			stream.filter(role -> {
				boolean equals = true;

				if (filters.containsKey("type"))
					equals = role.getType().toString().equalsIgnoreCase(filters.get("type"));
				if (equals && filters.containsKey("user_id"))
					equals = String.valueOf(role.getUser().getId()).equalsIgnoreCase(filters.get("user_id"));

				return equals;
			});
		return stream
				.map(role -> UserRolesDAO.create(role))
				.collect(Collectors.toList());
	}

	public List<UserSettingsDAO> getSettings(Map<String, String> filters, long offset, int limit) {
		Page<UserSettings> settings = userSettingsRepository.findAll(new OffSetPaging(offset, limit));
		Stream<UserSettings> stream = settings.stream();
		
		if (filters != null)
			stream.filter(setting -> {
				boolean equals = true;
				
				if (equals && filters.containsKey("user_id"))
					equals = String.valueOf(setting.getUser().getId()).equalsIgnoreCase(filters.get("user_id"));
				
				return equals;
			});
		return stream
				.map(setting -> UserSettingsDAO.create(setting))
				.collect(Collectors.toList());
	}

	public UserDAOResponse getById(Long id) {
		User user = userRepository.findById(id).orElse(null);
		
		return UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()), userSettingsRepository.findByUserId(user.getId()));
	}
}