package com.renergetic.userapi.service;

import java.util.List;
import java.util.Optional;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.userapi.dao.UserDAOResponse;
import com.renergetic.userapi.exception.IdAlreadyDefinedException;
import com.renergetic.userapi.exception.IdNoDefinedException;
import com.renergetic.userapi.exception.NotFoundException;
import com.renergetic.userapi.model.Asset;
import com.renergetic.userapi.model.AssetType;
import com.renergetic.userapi.model.UUID;
import com.renergetic.userapi.model.User;
import com.renergetic.userapi.model.UserSettings;
import com.renergetic.userapi.repository.AssetRepository;
import com.renergetic.userapi.repository.AssetTypeRepository;
import com.renergetic.userapi.repository.UserRepository;
import com.renergetic.userapi.repository.UserSettingsRepository;
import com.renergetic.userapi.repository.UuidRepository;

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
	
	public UserDAOResponse save(UserRepresentation user) {

        if (user.getId() != null && userRepo.existsByKeycloakId(user.getId()))
            throw new IdAlreadyDefinedException("Already exists a user with ID %s", user.getId());
        
        User entity = new User();
        
        entity.setKeycloakId(user.getId());
        entity.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        entity = userRepo.save(entity);
        settingsRepo.save(new UserSettings(entity, "{}"));
        
        AssetType type = typeRepo.findByName("user")
        		.orElse(typeRepo.save(new AssetType("user")));


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
        
		User entity = userRepo.findByKeycloakId(user.getId())
				.orElseThrow(() -> new IdNoDefinedException("No exists a user with ID %s", user.getId()));
        
        entity = userRepo.save(entity);
        settingsRepo.save(new UserSettings(entity, "{}"));
        
        Asset asset = assetRepo.findByUserId(entity.getId()).orElse(new Asset());

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
        
		User entity = userRepo.findByKeycloakId(user.getId())
				.orElseThrow(() -> new IdNoDefinedException("No exists a user with ID %s", user.getId()));
        
        settingsRepo.deletByUserId(entity.getId());
        assetRepo.deleteByUserId();
        
        entity.setKeycloakId(user.getId());
        entity.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        entity = userRepo.save(entity);
        settingsRepo.save(new UserSettings(entity, "{}"));
        
        AssetType type = typeRepo.findByName("user")
        		.orElse(typeRepo.save(new AssetType("user")));


        Asset asset = new Asset();
        asset.setName(user.getUsername());
        asset.setLabel(user.getUsername());
        asset.setType(type);
        asset.setUser(entity);
        asset.setUuid(uuidRepo.saveAndFlush(new UUID()));
        
        assetRepo.save(asset);
        
        return UserDAOResponse.create(user, null, null);
	}
	

    public UserSettings getSettings(String keycloakId) {
        User user = userRepo.findByKeycloakId(keycloakId)
        		.orElseThrow(() -> new NotFoundException("User with id %s found", keycloakId));

        List<UserSettings> settings = settingsRepo.findByUserId(user.getId());

        return settings.isEmpty()? new UserSettings(user, "{}") : settings.get(0);
    }

    public UserSettings saveSettings(String keycloakId, String settings) {
    	
        User user = userRepo.findByKeycloakId(keycloakId)
        		.orElseThrow(() -> new NotFoundException("User with id %s found", keycloakId));
        
        List<UserSettings> actualSettings = settingsRepo.findByUserId(user.getId());

    	UserSettings save = new UserSettings(user, settings);
    	
        if (!actualSettings.isEmpty()) {
        	save = actualSettings.get(0);
			save.setSettingsJson(settings);
        }
        return settingsRepo.save(save);
    }
}
