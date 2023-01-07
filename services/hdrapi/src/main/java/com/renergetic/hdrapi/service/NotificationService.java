package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.NotificationDAO;
import com.renergetic.hdrapi.exception.InvalidArgumentException;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Notification;
import com.renergetic.hdrapi.model.NotificationMessages;
import com.renergetic.hdrapi.repository.NotificationMessagesRepository;
import com.renergetic.hdrapi.repository.NotificationRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class NotificationService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	NotificationRepository repository;
	
	@Autowired
	NotificationMessagesRepository repositoryMessages;
	
	public NotificationDAO save(NotificationDAO notification) {
		if (!validateMessage(notification.getMessage()))
			throw new InvalidArgumentException("Invalid notification message");
		if (notification.getId() == null  || !repository.existsById(notification.getId())) {
			return NotificationDAO.create(repository.save(notification.mapToEntity()));
		} else throw new InvalidCreationIdAlreadyDefinedException("Notification with id " + notification.getId() + " already exists");
	}
	
	public NotificationDAO update(NotificationDAO notification) {
		if (!validateMessage(notification.getMessage()))
			throw new InvalidArgumentException("Invalid notification message");
		if (notification.getId() != null  || repository.existsById(notification.getId())) {
			return NotificationDAO.create(repository.save(notification.mapToEntity()));
		} else throw new InvalidNonExistingIdException("Notification with id " + notification.getId() + " doesn't exists");
	}
	
	public void deleteById(Long id) {
		if (id != null  || repository.existsById(id)) {
			repository.deleteById(id);
		} else throw new InvalidNonExistingIdException("Notification with id " + id + " doesn't exists");
	}
	
	public List<NotificationDAO> get(Long offset, Integer limit) {
		List<NotificationDAO> notifications = repository.findAll(new OffSetPaging(offset, limit))
				.stream().map(NotificationDAO::create).collect(Collectors.toList());

        if (notifications.size() > 0)
            return notifications;
        else throw new NotFoundException("No notifications are found");
	}
	
	public NotificationDAO getById(Long id) {
		Notification notification =  repository.findById(id).orElse(null);

        if (notification != null)
            return NotificationDAO.create(notification);
        else throw new NotFoundException("No notification with id " + id + " found");
	}
	
	public List<NotificationDAO> getByAssetId(Long assetId) {
		List<NotificationDAO> notifications =  repository.findByAssetId(assetId)
				.stream().map(NotificationDAO::create).collect(Collectors.toList());

        if (notifications.size() > 0)
            return notifications;
        else throw new NotFoundException("No notifications are found related with asset " + assetId);
	}
	
	public String saveMessage(String message) {
		if (repositoryMessages.existsById(message))
			throw new InvalidArgumentException("Message already exists");
		else
			return repositoryMessages.save(new NotificationMessages(message)).getMessage();
	}
	
	public void deleteMessage(String message) {
		if (message != null  || repositoryMessages.existsById(message)) {
			repositoryMessages.deleteById(message);
		} else throw new InvalidNonExistingIdException("Message '" + message + "' doesn't exists");
	}
	
	public List<String> getMesagges(Long offset, Integer limit) {
		return repositoryMessages.findAll(new OffSetPaging(offset, limit))
				.stream().map(NotificationMessages::toString).collect(Collectors.toList());
	}
	
	public Boolean validateMessage(String message) {
		if (repositoryMessages.count() > 0)
			return repositoryMessages.findAll()
					.stream().anyMatch(allowedMess -> allowedMess.getMessage().equals(message));
		else return true;
	}
}
