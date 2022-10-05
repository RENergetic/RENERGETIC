package com.renergetic.hdrapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.renergetic.hdrapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.AssetDAOResponse;
import com.renergetic.hdrapi.dao.UserDAORequest;
import com.renergetic.hdrapi.dao.UserDAOResponse;
import com.renergetic.hdrapi.dao.UserRolesDAO;
import com.renergetic.hdrapi.dao.UserSettingsDAO;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.AssetTypeCategory;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.model.UserRoles;
import com.renergetic.hdrapi.model.UserSettings;
import com.renergetic.hdrapi.repository.information.AssetDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

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
	@Autowired
	UuidRepository uuidRepository;
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	AssetDetailsRepository assetDetailsRepository;
	@Autowired
	MeasurementRepository measurementRepository;

	// USER CRUD OPERATIONS
	public UserDAOResponse save(UserDAORequest user) {
		if(user.getId() !=  null && userRepository.existsById(user.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a user with ID " + user.getId());
		
		User userEntity = user.mapToEntity();
		userEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return UserDAOResponse.create(userRepository.save(userEntity), null, null);
	}

	public UserRolesDAO saveRole(UserRolesDAO role) {
		if ( userRepository.existsById(role.getUserId()) ) {
			if(role.getId() !=  null && userRolesRepository.existsById(role.getId()))
	    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a user role with ID " + role.getId());
			
			return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
		} else throw new NotFoundException("The user with id " + role.getUserId() + " don't exists, the role can't be created");
	}

	public UserSettingsDAO saveSetting(UserSettingsDAO settings) {
		if ( userRepository.existsById(settings.getUserId()) ) {
			if(settings.getId() !=  null && userSettingsRepository.existsById(settings.getId()))
	    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a user settings with ID " + settings.getId());
			
			return UserSettingsDAO.create(userSettingsRepository.save(settings.mapToEntity()));
		} else throw new NotFoundException("The user with id " + settings.getUserId() + " don't exists, the role can't be created");
	}
	
	public boolean deleteById(Long id) {
		if (id != null && userRepository.existsById(id)) {
			userRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No user with id " + id + " found");
	}
	
	public boolean deleteRoleById(Long id) {
		if (id != null && userRolesRepository.existsById(id)) {
			userRolesRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No user role with id " + id + " found");
	}
	
	public boolean deleteSettingById(Long id) {
		if (id != null && userSettingsRepository.existsById(id)) {
			userSettingsRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No setting with id " + id + " found");
	}

	public UserDAOResponse update(UserDAORequest user, Long id) {
		if ( userRepository.existsById(id) ) {
			user.setId(id);
			return UserDAOResponse.create(userRepository.save(user.mapToEntity()), null, null);
		} else throw new InvalidNonExistingIdException("No user with id " + id + " found");
	}

	public UserRolesDAO updateRole(UserRolesDAO role, Long id) {
		if ( userRolesRepository.existsById(id) && userRepository.existsById(role.getUserId()) ) {
			role.setId(id);
			return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
		} else throw new InvalidNonExistingIdException("No role with id " + id + " found");
	}

	public UserSettingsDAO updateSetting(UserSettingsDAO setting, Long id) {
		if ( userSettingsRepository.existsById(id) && userRepository.existsById(setting.getUserId()) ) {
			setting.setId(id);
			return UserSettingsDAO.create(userSettingsRepository.save(setting.mapToEntity()));
		} else throw new InvalidNonExistingIdException("No setting with id " + id + " found");
	}

	public List<UserDAOResponse> get(Map<String, String> filters, long offset, int limit) {
	Page<User> users = userRepository.findAll(new OffSetPaging(offset, limit));
		Stream<User> stream = users.stream();
		
//		if (filters != null)
//			stream.filter(user -> {
//				boolean equals = true;
//				
////				if (filters.containsKey("name"))
////					equals = user.getName().equalsIgnoreCase(filters.get("name"));
////				return equals;
//			});
		List<UserDAOResponse> list = stream
				.map(user -> UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()), userSettingsRepository.findByUserId(user.getId())))
				.collect(Collectors.toList());
		
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No users found");
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
		List<UserRolesDAO> list = stream
				.map(role -> UserRolesDAO.create(role))
				.collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No roles found");
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
		List<UserSettingsDAO> list = stream
				.map(setting -> UserSettingsDAO.create(setting))
				.collect(Collectors.toList());
		
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No settings found");
	}

	public UserDAOResponse getById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("No user with id " + id + " found"));
		
		return UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()), userSettingsRepository.findByUserId(user.getId()));
	}
	
	public List <AssetDAOResponse> getAssets(Long id){
		
		User user= userRepository.findById(id).orElseThrow(() -> new NotFoundException("No user with id " + id + " found"));
		if(user==null) {
			throw new InvalidNonExistingIdException("No user realated with id"+id);
		}else {
			List<Asset> userAssets = assetRepository.findByUser(user);
			List<AssetDAOResponse> assets = new ArrayList<>();

			if(userAssets != null && userAssets.size() > 0) {
				for (Asset userAsset : userAssets)
					assets.addAll(userAsset.getAssets().stream()
						.filter(obj -> obj.getType().getTypeCategory().equals(AssetTypeCategory.structural))
						.map(obj -> AssetDAOResponse.create(obj, assetDetailsRepository.findByAssetId(obj.getId())))
						.collect(Collectors.toList()));
				return assets;
			}
			else throw new NotFoundException("User " + id + " hasn't related asset");
		}
	}

	public List <AssetDAOResponse> getAssetsByCategory(Long id, Long categoryId, long offset, int limit){
		List<AssetDAOResponse> list = assetRepository.findByUserIdAndCategoryId(id, categoryId, offset, limit)
				.stream().map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAsset(asset)))
				.collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No roles found");
	}
}
