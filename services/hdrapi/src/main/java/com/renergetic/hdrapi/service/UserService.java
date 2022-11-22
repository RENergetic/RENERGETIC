package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.*;
import com.renergetic.hdrapi.repository.*;
import com.renergetic.hdrapi.repository.information.AssetDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (user.getId() != null && userRepository.existsById(user.getId()))
            throw new InvalidCreationIdAlreadyDefinedException("Already exists a user with ID " + user.getId());

        User userEntity = user.mapToEntity();
        userEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        return UserDAOResponse.create(userRepository.save(userEntity), null, null);
    }

    public UserRolesDAO saveRole(UserRolesDAO role) {
        if (role.getUserId() != null && userRepository.existsById(role.getUserId())) {
            if (role.getId() != null && userRolesRepository.existsById(role.getId()))
                throw new InvalidCreationIdAlreadyDefinedException(
                        "Already exists a user role with ID " + role.getId());

            return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
        } else throw new NotFoundException(
                "The user with id " + role.getUserId() + " don't exists, the role can't be created");
    }

    public UserSettingsDAO saveSetting(UserSettingsDAO settings) {
        if (settings.getUserId() != null && userRepository.existsById(settings.getUserId())) {
            if (settings.getId() != null && userSettingsRepository.existsById(settings.getId()))
                throw new InvalidCreationIdAlreadyDefinedException(
                        "Already exists a user settings with ID " + settings.getId());

            return UserSettingsDAO.create(userSettingsRepository.save(settings.mapToEntity()));
        } else throw new NotFoundException(
                "The user with id " + settings.getUserId() + " don't exists, the role can't be created");
    }

    public String getSettings(Long userId) {
        var user = userRepository.getById(userId);
        if (user == null) {
            throw new NotFoundException("user not found");
        }
        //TODO: return only 1 settings row
        List<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        UserSettings us;
        if (settings.isEmpty()) {
            us = new UserSettings();
            us.setUser(user);
            us.setSettingsJson("{}");
            us = userSettingsRepository.save(us);
        } else {
            us = settings.get(0);
        }
        return us.getSettingsJson();
    }

    public String saveSettings(Long userId, String settingsJson) {
        var user = userRepository.getById(userId);
        if (user == null) {
            throw new NotFoundException("user not found");
        }
        //TODO: return only 1 settings row
        List<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        UserSettings us;
        if (settings.isEmpty()) {
            us = new UserSettings();
            us.setUser(user);
            us.setSettingsJson(settingsJson);
        } else {
            us = settings.get(0);
            us.setSettingsJson(settingsJson);
        }
        return userSettingsRepository.save(us).getSettingsJson();

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
        if (id != null && userRepository.existsById(id)) {
            user.setId(id);
            return UserDAOResponse.create(userRepository.save(user.mapToEntity()), null, null);
        } else throw new InvalidNonExistingIdException("No user with id " + id + " found");
    }

    public UserRolesDAO updateRole(UserRolesDAO role, Long id) {
        if (userRolesRepository.existsById(id) && userRepository.existsById(role.getUserId())) {
            role.setId(id);
            return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
        } else throw new InvalidNonExistingIdException("No role with id " + id + " found");
    }

    public UserSettingsDAO updateSettings(UserSettingsDAO setting, Long id) {
        if (userSettingsRepository.existsById(id) && userRepository.existsById(setting.getUserId())) {
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
                .map(user -> UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()),
                        userSettingsRepository.findByUserId(user.getId())))
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
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No user with id " + id + " found"));

        return UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()),
                userSettingsRepository.findByUserId(user.getId()));
    }

    public List<AssetDAOResponse> getAssets(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No user with id " + id + " found"));
        if (user == null) {
            throw new InvalidNonExistingIdException("No user realated with id" + id);
        } else {
            List<Asset> userAssets = assetRepository.findByUser(user);
            List<AssetDAOResponse> assets = new ArrayList<>();

            if (userAssets != null && userAssets.size() > 0) {
                for (Asset userAsset : userAssets)
                    assets.addAll(userAsset.getAssets().stream()
//							.filter(obj -> obj.getAssetCategory().getName().equals(AssetTypeCategory.structural))
                            .map(obj -> AssetDAOResponse.create(obj, assetDetailsRepository.findByAssetId(obj.getId())))
                            .collect(Collectors.toList()));
                return assets;
            } else throw new NotFoundException("User " + id + " hasn't related asset");
        }
    }

    public List<AssetDAOResponse> getAssetsByCategory(Long id, Long categoryId, long offset, int limit) {
        List<AssetDAOResponse> list = assetRepository.findByUserIdAndCategoryId(id, categoryId, offset, limit)
                .stream().map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset),
                        measurementRepository.findByAsset(asset)))
                .collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No roles found");
    }
}
