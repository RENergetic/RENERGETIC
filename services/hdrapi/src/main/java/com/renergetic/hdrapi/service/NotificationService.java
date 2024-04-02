package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.utilities.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.NotificationDefinitionDAO;
import com.renergetic.common.dao.NotificationScheduleDAO;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.NotificationDefinition;
import com.renergetic.common.model.NotificationSchedule;
import com.renergetic.common.repository.NotificationDefinitionRepository;
import com.renergetic.common.repository.NotificationScheduleRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class NotificationService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    NotificationScheduleRepository repositorySchedule;

    @Autowired
    NotificationDefinitionRepository repositoryDefinition;

    @Autowired
    AssetRepository assetRepository;

    public NotificationScheduleDAO save(NotificationScheduleDAO notification) {
        NotificationDefinition definition =
                repositoryDefinition.findByCode(notification.getNotificationCode()).orElse(null);
        if (definition == null)
            throw new InvalidArgumentException("Invalid notification definition code");
        if (notification.getId() == null || !repositorySchedule.existsById(notification.getId())) {
            return NotificationScheduleDAO.create(repositorySchedule.save(notification.mapToEntity(definition)));
        } else throw new InvalidCreationIdAlreadyDefinedException(
                "Notification with id " + notification.getId() + " already exists");
    }

    public NotificationScheduleDAO update(NotificationScheduleDAO notification) {
        NotificationDefinition definition =
                repositoryDefinition.findByCode(notification.getNotificationCode()).orElse(null);
        if (definition == null)
            throw new InvalidArgumentException("Invalid notification definition code");
        if (notification.getId() != null && repositorySchedule.existsById(notification.getId())) {
            return NotificationScheduleDAO.create(repositorySchedule.save(notification.mapToEntity(definition)));
        } else
            throw new InvalidNonExistingIdException("Notification with id " + notification.getId() + " doesn't exists");
    }

    public void deleteById(Long id) {
        if (id != null || repositorySchedule.existsById(id)) {
            repositorySchedule.deleteById(id);
        } else throw new InvalidNonExistingIdException("Notification with id " + id + " doesn't exists");
    }

    public List<NotificationScheduleDAO> get(Long offset, Integer limit, Boolean showExpired) {
        List<NotificationScheduleDAO> notifications = (showExpired ?
                repositorySchedule.findAll(new OffSetPaging(offset, limit)).toList()
                : repositorySchedule.findNotExpired(new OffSetPaging(offset, limit))
        ).stream().map(NotificationScheduleDAO::create).collect(Collectors.toList());

        return notifications;
    }

    public List<NotificationScheduleDAO> get(Long dateFrom, Long dateTo, Long offset, Integer limit) {
        List<NotificationScheduleDAO> notifications =
                repositorySchedule.listAll(DateConverter.toLocalDateTime(dateFrom),
                                dateTo != null ? DateConverter.toLocalDateTime(dateTo) : null, new OffSetPaging(offset, limit))
                        .stream().map(NotificationScheduleDAO::create).collect(Collectors.toList());


        return notifications;

    }

    public NotificationScheduleDAO getById(Long id) {
        NotificationSchedule notification = repositorySchedule.findById(id).orElse(null);

        if (notification != null)
            return NotificationScheduleDAO.create(notification);
        else throw new NotFoundException("No notification with id " + id + " found");
    }

    public List<NotificationScheduleDAO> getByAssetId(Long assetId, Long offset, Integer limit, Boolean showExpired) {
        if (!assetRepository.existsById(assetId)) {
            throw new NotFoundException("No asset with an id: " + assetId);
        }
        return (showExpired ?
                repositorySchedule.findAll(new OffSetPaging(offset, limit)).toList()
                : repositorySchedule.findNotExpired(new OffSetPaging(offset, limit))
        ).stream().map(NotificationScheduleDAO::create).collect(Collectors.toList());

    }

    public NotificationDefinitionDAO saveDefinition(NotificationDefinitionDAO notification) {
        if (repositoryDefinition.existsByIdOrCode(notification.getId(), notification.getCode()))
            throw new InvalidArgumentException("Notification definitio already exists");
        else
            return NotificationDefinitionDAO.create(repositoryDefinition.save(notification.mapToEntity()));
    }

    public void deleteDefinition(Long notificationId) {
        if (notificationId != null && repositoryDefinition.existsById(notificationId)) {
            repositoryDefinition.deleteById(notificationId);
        } else
            throw new InvalidNonExistingIdException("NotificationDefinition '" + notificationId + "' doesn't exists");
    }

    public List<NotificationDefinitionDAO> getDefinition(Long offset, Integer limit) {
        return repositoryDefinition.findAll(new OffSetPaging(offset, limit))
                .stream().map(NotificationDefinitionDAO::create).collect(Collectors.toList());
    }


}
