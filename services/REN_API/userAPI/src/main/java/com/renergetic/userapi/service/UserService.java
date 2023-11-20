package com.renergetic.userapi.service;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.UserDAOResponse;
import com.renergetic.common.exception.IdAlreadyDefinedException;
import com.renergetic.common.exception.IdNoDefinedException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetType;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.User;
import com.renergetic.common.model.UserSettings;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.AssetTypeRepository;
import com.renergetic.common.repository.UserRepository;
import com.renergetic.common.repository.UserSettingsRepository;
import com.renergetic.common.repository.UuidRepository;

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

        if (user.getId() != null && userRepo.findByKeycloakId(user.getId()) != null) //TODO: replace with existsByKeycloakId
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
        
		User entity = userRepo.findByKeycloakId(user.getId());

        if (entity == null)
            throw new IdNoDefinedException("No exists a user with ID %s", user.getId());
        
        entity = userRepo.save(entity);
        settingsRepo.save(new UserSettings(entity, "{}"));
        
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

        settingsRepo.deleteByUserId(entity.getId());

        // TODO: create method at the repository deleteByUserId and replace following code
        assetRepo.findByUserId(entity.getId());
        userRepo.delete(entity);
        
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
}
