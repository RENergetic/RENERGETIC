package com.renergetic.userapi.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.AssetDAOResponse;
import com.renergetic.common.dao.NotificationScheduleDAO;
import com.renergetic.common.dao.UserDAOResponse;
import com.renergetic.common.exception.IdAlreadyDefinedException;
import com.renergetic.common.exception.IdNoDefinedException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetType;
import com.renergetic.common.model.NotificationSchedule;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.User;
import com.renergetic.common.model.UserSettings;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.AssetTypeRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.common.repository.NotificationScheduleRepository;
import com.renergetic.common.repository.UserRepository;
import com.renergetic.common.repository.UserSettingsRepository;
import com.renergetic.common.repository.UuidRepository;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.common.utilities.OffSetPaging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UuidRepository uuidRepo;

	@Autowired
	UserSettingsRepository settingsRepo;

	@Autowired
	AssetTypeRepository typeRepo;

	@Autowired
	AssetRepository assetRepo;

    @Autowired
    AssetDetailsRepository assetDetailsRepo;

    @Autowired
    NotificationScheduleRepository notificationRepo;

    @Autowired
    MeasurementRepository measurementRepo;
	
	public UserDAOResponse save(UserRepresentation user) {

        if (user.getId() != null && userRepo.findByKeycloakId(user.getId()) != null) //TODO: replace with existsByKeycloakId
            throw new IdAlreadyDefinedException("Already exists a user with ID %s", user.getId());
        
        User entity = new User();
        
        entity.setKeycloakId(user.getId());
        entity.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        entity = userRepo.save(entity);
        settingsRepo.save(new UserSettings(entity, "{}"));
        
        AssetType type = typeRepo.findByName("user").orElse(null);
        if (type == null) {
            log.warn("Asset type 'user' doesn't exists. It's being created");
        	type = typeRepo.save(new AssetType("user"));
        }

        Asset asset = new Asset();
        asset.setName(user.getUsername());
        asset.setLabel(user.getUsername());
        asset.setType(type);
        asset.setUser(entity);
        asset.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        assetRepo.save(asset);
        
        return UserDAOResponse.create(user, null, null);
	}
	
	public UserDAOResponse update(UserRepresentation user) {

        if (user.getId() == null)
            throw new IdNoDefinedException("A user Keycloak ID is required");
        
		User entity = userRepo.findByKeycloakId(user.getId());

        if (entity == null)
            throw new IdNoDefinedException("No exists a user with ID %s", user.getId());
        
        // Create user
        entity = userRepo.save(entity);
        // Create user settings
        settingsRepo.save(new UserSettings(entity, "{}"));
        // Create an asset related with the user
        Asset asset = assetRepo.findByUserId(entity.getId());
        asset = asset != null ? asset : new Asset();

        AssetType type = typeRepo.findByName("user")
        		.orElse(typeRepo.save(new AssetType("user")));

        asset.setName(user.getUsername());
        asset.setLabel(user.getUsername());
        asset.setType(type);
        asset.setUser(entity);
        asset.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        assetRepo.save(asset);
        
        return UserDAOResponse.create(user, null, null);
	}
	
	public UserDAOResponse delete(UserRepresentation user) {

        if (user.getId() == null)
            throw new IdNoDefinedException("A user Keycloak ID is required");
        
		User entity = userRepo.findByKeycloakId(user.getId());

        if (entity == null)
            throw new IdNoDefinedException("No exists a user with ID %s", user.getId());

        // Remove user settings
        settingsRepo.deleteByUserId(entity.getId());
        // Remove asset related with the user
        // TODO: create method at the repository deleteByUserId and replace following code
        Asset userAsset = assetRepo.findByUserId(entity.getId());
        if (userAsset != null)
            assetRepo.delete(userAsset);
        // Remove user
        userRepo.delete(entity);
        
        return UserDAOResponse.create(user, null, null);
	}
	

    public UserSettings getSettings(String keycloakId) {
        User user = userRepo.findByKeycloakId(keycloakId);
        
        if (user == null)
            throw new NotFoundException("User with id %s found", keycloakId);

        List<UserSettings> settings = settingsRepo.findByUserId(user.getId());

        return settings.isEmpty()? new UserSettings(user, "{}") : settings.get(0);
    }

    public UserSettings saveSettings(String keycloakId, String settings) {
    	
        User user = userRepo.findByKeycloakId(keycloakId);

        if(user == null)
        	throw new NotFoundException("User with id %s found", keycloakId);
        
        List<UserSettings> actualSettings = settingsRepo.findByUserId(user.getId());

    	UserSettings save = new UserSettings(user, settings);
    	
        if (!actualSettings.isEmpty()) {
        	save = actualSettings.get(0);
			save.setSettingsJson(settings);
        }
        return settingsRepo.save(save);
    }

    public List<NotificationScheduleDAO> getNotifications(String keycloakId, Long offset, Integer limit, Boolean showExpired) {
        // TODO: filter by user id
        List<NotificationSchedule> schedules = Boolean.TRUE.equals(showExpired) ?
            notificationRepo.findAll(new OffSetPaging(offset, limit)).toList()
            : notificationRepo.findNotExpired(new OffSetPaging(offset, limit));
        
        return schedules.stream().map(NotificationScheduleDAO::create).collect(Collectors.toList());
    }
    
    public List<AssetDAOResponse> getAssets(Long id) {

        User user = userRepo.findById(id).orElse(null);

        if(user == null)
        	throw new NotFoundException("User with id %s found", id);

        List<Asset> userAssets = assetRepo.findByUser(user);

        if (userAssets.isEmpty()) {
            List<AssetDAOResponse> assets = new LinkedList<>();
            for (Asset userAsset : userAssets)
                assets.addAll(userAsset.getConnections().stream()
                        .map(obj -> {
                            AssetDAOResponse dao = AssetDAOResponse.create(obj.getConnectedAsset(),
                                    assetDetailsRepo.findByAssetId(obj.getConnectedAsset().getId()));
                            dao.setConnectionType(obj.getConnectionType());
                            return dao;
                        })
                        .collect(Collectors.toList()));
            return assets;
        } else throw new NotFoundException("User " + id + " hasn't related asset");
    }

    public List<AssetDAOResponse> getAssetsByCategory(Long id, Long categoryId, long offset, int limit) {
        List<AssetDAOResponse> list = assetRepo.findByUserIdAndCategoryId(id, categoryId, offset, limit)
                .stream().map(asset -> AssetDAOResponse.create(asset, assetRepo.findByParentAsset(asset),
                        measurementRepo.findByAsset(asset)))
                .collect(Collectors.toList());

        if (list.isEmpty())
            return list;
        else throw new NotFoundException("No roles found");
    }
}
