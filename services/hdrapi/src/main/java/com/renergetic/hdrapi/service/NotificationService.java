package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.NotificationDAO;
import com.renergetic.hdrapi.dao.NotificationDefinitionDAO;
import com.renergetic.hdrapi.dao.NotificationScheduleDAO;
import com.renergetic.hdrapi.exception.InvalidArgumentException;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.NotificationDefinition;
import com.renergetic.hdrapi.model.NotificationSchedule;
import com.renergetic.hdrapi.repository.NotificationDefinitionRepository;
import com.renergetic.hdrapi.repository.NotificationScheduleRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class NotificationService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	NotificationScheduleRepository repositorySchedule;
	
	@Autowired
	NotificationDefinitionRepository repositoryDefinition;
	
	public NotificationDAO save(NotificationScheduleDAO notification) {
		NotificationDefinition definition = repositoryDefinition.findByCode(notification.getNotificationCode()).orElse(null);
		if (definition == null)
			throw new InvalidArgumentException("Invalid notification definition code");
		if (notification.getId() == null  || !repositorySchedule.existsById(notification.getId())) {
			return NotificationDAO.create(repositorySchedule.save(notification.mapToEntity(definition)));
		} else throw new InvalidCreationIdAlreadyDefinedException("Notification with id " + notification.getId() + " already exists");
	}
	
	public NotificationDAO update(NotificationScheduleDAO notification) {
		NotificationDefinition definition = repositoryDefinition.findByCode(notification.getNotificationCode()).orElse(null);
		if (definition == null)
			throw new InvalidArgumentException("Invalid notification definition code");
		if (notification.getId() != null  && repositorySchedule.existsById(notification.getId())) {
			return NotificationDAO.create(repositorySchedule.save(notification.mapToEntity(definition)));
		} else throw new InvalidNonExistingIdException("Notification with id " + notification.getId() + " doesn't exists");
	}
	
	public void deleteById(Long id) {
		if (id != null  || repositorySchedule.existsById(id)) {
			repositorySchedule.deleteById(id);
		} else throw new InvalidNonExistingIdException("Notification with id " + id + " doesn't exists");
	}
	
	public List<NotificationDAO> get(Long offset, Integer limit, Boolean showExpired) {
		List<NotificationDAO> notifications = (showExpired? 
					repositorySchedule.findAll(new OffSetPaging(offset, limit)).toList()
					: repositorySchedule.findNotExpired(new OffSetPaging(offset, limit))
				).stream().map(NotificationDAO::create).collect(Collectors.toList());

        if (notifications.size() > 0)
            return notifications;
        else throw new NotFoundException("No notifications are found");
	}
	
	public NotificationDAO getById(Long id) {
		NotificationSchedule notification =  repositorySchedule.findById(id).orElse(null);

        if (notification != null)
            return NotificationDAO.create(notification);
        else throw new NotFoundException("No notification with id " + id + " found");
	}
	
	public List<NotificationDAO> getByAssetId(Long assetId, Long offset, Integer limit, Boolean showExpired) {
		List<NotificationDAO> notifications =  (showExpired? 
					repositorySchedule.findAll(new OffSetPaging(offset, limit)).toList()
					: repositorySchedule.findNotExpired(new OffSetPaging(offset, limit))
				).stream().map(NotificationDAO::create).collect(Collectors.toList());

        if (notifications.size() > 0)
            return notifications;
        else throw new NotFoundException("No notifications are found related with asset " + assetId);
	}
	
	public NotificationDefinitionDAO saveDefinition(NotificationDefinitionDAO notification) {
		if (repositoryDefinition.existsByIdOrCode(notification.getId(), notification.getCode()))
			throw new InvalidArgumentException("Notification definitio already exists");
		else
			return NotificationDefinitionDAO.create(repositoryDefinition.save(notification.mapToEntity()));
	}
	
	public void deleteDefinition(Long notificationId) {
		if (notificationId != null  && repositoryDefinition.existsById(notificationId)) {
			repositoryDefinition.deleteById(notificationId);
		} else throw new InvalidNonExistingIdException("NotificationDefinition '" + notificationId + "' doesn't exists");
	}
	
	public List<NotificationDefinitionDAO> getDefinition(Long offset, Integer limit) {
		return repositoryDefinition.findAll(new OffSetPaging(offset, limit))
				.stream().map(NotificationDefinitionDAO::create).collect(Collectors.toList());
	}
}
