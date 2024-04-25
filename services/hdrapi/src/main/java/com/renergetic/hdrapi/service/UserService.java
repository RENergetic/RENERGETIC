package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.ErrorMessage;
import com.renergetic.common.dao.AssetDAOResponse;
import com.renergetic.common.dao.UserDAORequest;
import com.renergetic.common.dao.UserDAOResponse;
import com.renergetic.common.dao.UserSettingsDAO;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;
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
    @Autowired
    AssetTypeRepository assetTypeRepository;

    @Autowired
    KeycloakService keycloakService;

    // USER CRUD OPERATIONS
   @Transactional
   public UserDAOResponse save(UserRepresentation keycloakUser, UserDAORequest user) {
       KeycloakWrapper client = keycloakService.getClient(null, true);
       if (Boolean.TRUE.equals(client.userExists(user.getUsername()))) {
           throw new DuplicateKeyException("User exists: " + user.getUsername());
       }
       UserRepresentation ur = client.createUser(user);
       user.setId(ur.getId());
       if (user.getId() != null && userRepository.findByKeycloakId(user.getId()) != null)
           throw new InvalidCreationIdAlreadyDefinedException("Already exists a user with ID " + user.getId());

       User userEntity = user.mapToEntity();
       userEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
       userEntity = userRepository.save(userEntity);
       userSettingsRepository.save(new UserSettings(userEntity, "{}"));
       Optional<AssetType> type = assetTypeRepository.findByName("user");
       AssetType assetType;
       if (type.isEmpty()) {
           assetType = new AssetType("user");
           assetTypeRepository.save(assetType);
       } else {
           assetType = type.get();
       }
       Asset asset = Asset.initUserAsset(user.getUsername(), userEntity, assetType, null);
       asset.setUuid(uuidRepository.saveAndFlush(new UUID()));
       assetRepository.save(asset);
       return UserDAOResponse.create(keycloakUser, null, null);
   }

    @Transactional
    public UserRepresentation create(KeycloakWrapper client, UserDAORequest request) {
        UserRepresentation kUser;
        if (client.userExists(request.getUsername())) {
            //TODO:
//            throw new DuplicateKeyException("User exists: "+ user.getUsername());
            kUser = client.getUserByUsername(request.getUsername());
        } else {
            kUser = client.createUser(request);
        }
        if (kUser.getId() == null && userRepository.findByKeycloakId(kUser.getId()) == null) {
            throw new InvalidNonExistingIdException(
                    "User not created, check Keycloak state (" + request.getUsername() + ")");
        }
        try {
            User userEntity = request.mapToEntity();
            userEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
            userEntity.setKeycloakId(kUser.getId());
            userEntity = userRepository.save(userEntity);
            userSettingsRepository.save(new UserSettings(userEntity, "{}"));
            Optional<AssetType> type = assetTypeRepository.findByName("user");
            AssetType assetType;
            if (type.isEmpty()) {
                assetType = new AssetType("user");
                assetTypeRepository.save(assetType);
            } else {
                assetType = type.get();
            }
            Asset asset = Asset.initUserAsset(request.getUsername(), userEntity, assetType, null);
            asset.setUuid(uuidRepository.saveAndFlush(new UUID()));
            assetRepository.save(asset);
        } catch (Exception ex) {
//            ex.printStackTrace();
            log.error(ex.getMessage());
            client.deleteUser(kUser.getId());
            throw new RuntimeException("User not created");
        }
        return kUser;
//        return UserDAOResponse.create(keycloakUser, null, null);
    }

    @Transactional
    public UserDAOResponse delete(UserRepresentation keycloakUser) {
        if (keycloakUser.getId() == null)
            throw new NotFoundException("Not exists a user with ID " + keycloakUser.getId());
        User entityUser = userRepository.findByKeycloakId(keycloakUser.getId());
        if (entityUser == null)
            throw new NotFoundException(
                    "Not exists a user with ID " + keycloakUser.getId() + ":" + keycloakUser.getUsername());
//        var uuid = entityUser.getUuid();
        userSettingsRepository.deleteByUserId(entityUser.getId());
        var userTypeId = assetTypeRepository.findByName("user").get().getId();
        assetRepository.clearUserId(entityUser.getId(), userTypeId);
        var asset = assetRepository.findByUserId(entityUser.getId());
        assetRepository.deleteById(asset.getId());
        uuidRepository.delete(asset.getUuid());
        userRepository.deleteById(entityUser.getId());
        uuidRepository.delete(entityUser.getUuid());
        return UserDAOResponse.create(keycloakUser, null, null);
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

//    public boolean deleteRoleById(Long id) {
//        if (id != null && userRolesRepository.existsById(id)) {
//            userRolesRepository.deleteById(id);
//            return true;
//        } else throw new InvalidNonExistingIdException("No user role with id " + id + " found");
//    }

    //    public boolean deleteSettingById(Long id) {
//        if (id != null && userSettingsRepository.existsById(id)) {
//            userSettingsRepository.deleteById(id);
//            return true;
//        } else throw new InvalidNonExistingIdException("No setting with id " + id + " found");
//    }
    @Transactional
    public UserDAOResponse update(UserDAORequest user, UserRepresentation userRepresentation) {
        if (userRepresentation == null)
            throw new InvalidNonExistingIdException("Not exists a user with ID " + user.getId());
        User userEntity = userRepository.findByKeycloakId(userRepresentation.getId());
        if (userEntity == null)
            throw new InvalidNonExistingIdException("Not exists a user with ID " + user.getId());
//        userEntity.set //set something and save
//        userEntity = userRepository.save(userEntity );
        Asset asset = assetRepository.findByUserId(userEntity.getId());
        if (asset != null) {
            asset.setName(userRepresentation.getUsername());
            asset.setLabel(userRepresentation.getUsername());
            assetRepository.save(asset);
        } else {
            //else todo:
        }
        return UserDAOResponse.create(userRepresentation, null, null);
    }


    public UserSettingsDAO updateSettings(UserSettingsDAO setting, Long id) {
        if (userSettingsRepository.existsById(id) && userRepository.existsById(setting.getUserId())) {
            setting.setId(id);
            return UserSettingsDAO.create(userSettingsRepository.save(setting.mapToEntity()));
        } else throw new InvalidNonExistingIdException("No setting with id " + id + " found");
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

//    public UserDAOResponse getById(Long id) {
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new NotFoundException("No user with id " + id + " found"));
//
//        return UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()),
//                userSettingsRepository.findByUserId(user.getId()));
//    }

    public List<AssetDAOResponse> getAssets(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id " + userId + " found"));
        if (user == null) {
            throw new InvalidNonExistingIdException("No user realated with id" + userId);
        } else {
            List<Asset> userAssets = assetRepository.findByUser(user);
            List<AssetDAOResponse> assets = new ArrayList<>();

            if (userAssets != null && userAssets.size() > 0) {
                for (Asset userAsset : userAssets)
                    assets.addAll(userAsset.getConnections().stream()
//							.filter(obj -> obj.getAssetCategory().getName().equals(AssetTypeCategory.structural))
                            .map(obj -> {
                                AssetDAOResponse dao = AssetDAOResponse.create(obj.getConnectedAsset(),
                                        assetDetailsRepository.findByAssetId(obj.getConnectedAsset().getId()));
                                dao.setConnectionType(obj.getConnectionType());
                                return dao;
                            })
                            .collect(Collectors.toList()));
                return assets;
            } else throw new NotFoundException("User " + userId + " hasn't related asset");
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
//    public UserRolesDAO saveRole(UserRolesDAO role) {
//        if (role.getUserId() != null && userRepository.existsById(role.getUserId())) {
//            if (role.getId() != null && userRolesRepository.existsById(role.getId()))
//                throw new InvalidCreationIdAlreadyDefinedException(
//                        "Already exists a user role with ID " + role.getId());
//
//            return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
//        } else throw new NotFoundException(
//                "The user with id " + role.getUserId() + " don't exists, the role can't be created");
//    }//    public UserRolesDAO updateRole(UserRolesDAO role, Long id) {
////        if (userRolesRepository.existsById(id) && userRepository.existsById(role.getUserId())) {
////            role.setId(id);
////            return UserRolesDAO.create(userRolesRepository.save(role.mapToEntity()));
////        } else throw new InvalidNonExistingIdException("No role with id " + id + " found");
////    } public List<UserRolesDAO> getRoles(Map<String, String> filters, long offset, int limit) {
//        Page<UserRoles> roles = userRolesRepository.findAll(new OffSetPaging(offset, limit));
//        Stream<UserRoles> stream = roles.stream();
//
//        if (filters != null)
//            stream.filter(role -> {
//                boolean equals = true;
//
//                if (filters.containsKey("type"))
//                    equals = role.getType().toString().equalsIgnoreCase(filters.get("type"));
//                if (equals && filters.containsKey("user_id"))
//                    equals = String.valueOf(role.getUser().getId()).equalsIgnoreCase(filters.get("user_id"));
//
//                return equals;
//            });
//        List<UserRolesDAO> list = stream
//                .map(role -> UserRolesDAO.create(role))
//                .collect(Collectors.toList());
//
//        if (list != null && list.size() > 0)
//            return list;
//        else throw new NotFoundException("No roles found");
//    }

//    public List<UserRolesDAO> getRoles(Map<String, String> filters, long offset, int limit) {
//        Page<UserRoles> roles = userRolesRepository.findAll(new OffSetPaging(offset, limit));
//        Stream<UserRoles> stream = roles.stream();
//
//        if (filters != null)
//            stream.filter(role -> {
//                boolean equals = true;
//
//                if (filters.containsKey("type"))
//                    equals = role.getType().toString().equalsIgnoreCase(filters.get("type"));
//                if (equals && filters.containsKey("user_id"))
//                    equals = String.valueOf(role.getUser().getId()).equalsIgnoreCase(filters.get("user_id"));
//
//                return equals;
//            });
//        List<UserRolesDAO> list = stream
//                .map(role -> UserRolesDAO.create(role))
//                .collect(Collectors.toList());
//
//        if (list != null && list.size() > 0)
//            return list;
//        else throw new NotFoundException("No roles found");
//    }
//public List<UserDAOResponse> get(Map<String, String> filters, long offset, int limit) {
//    Page<User> users = userRepository.findAll(new OffSetPaging(offset, limit));
//    Stream<User> stream = users.stream();
//
////		if (filters != null)
////			stream.filter(user -> {
////				boolean equals = true;
////
//////				if (filters.containsKey("name"))
//////					equals = user.getName().equalsIgnoreCase(filters.get("name"));
//////				return equals;
////			});
//    List<UserDAOResponse> list = stream
//            .map(user -> UserDAOResponse.create(user, userRolesRepository.findByUserId(user.getId()),
//                    userSettingsRepository.findByUserId(user.getId())))
//            .collect(Collectors.toList());
//
//    if (list != null && list.size() > 0)
//        return list;
//    else throw new NotFoundException("No users found");
//}
