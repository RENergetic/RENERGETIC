package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.LogDAO;
import com.renergetic.common.dao.NotificationDefinitionDAO;
import com.renergetic.common.dao.NotificationScheduleDAO;
import com.renergetic.common.dao.wrapper.PagedDAO;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.LogRepository;
import com.renergetic.common.repository.NotificationDefinitionRepository;
import com.renergetic.common.repository.NotificationScheduleRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private NotificationDefinitionRepository notificationDefinitionRepository;
    @Autowired
    private NotificationScheduleRepository notificationScheduleRepository;

    public PagedDAO<LogDAO> list(Optional<Long> offset, Optional<Integer> limit, Optional<String> orderBy,
                                 Optional<List<LogSeverity>> severity, Optional<List<LogService>> service,
                                 Optional<String> from, Optional<String> to) {
        Pageable paging;
        paging = orderBy.map(s -> new OffSetPaging(offset.orElse(0L), limit.orElse(20), Sort.by(s)))
                .orElseGet(() -> new OffSetPaging(offset.orElse(0L), limit.orElse(20), Sort.by("timestamp").descending()));

        if(severity.isPresent() || service.isPresent() || from.isPresent() || to.isPresent()){
            Specification<Log> specification = null;
            if(severity.isPresent())
                specification = Specification.where(filterSeverity(severity.get()));
            if(service.isPresent())
                specification = specification == null ? Specification.where(filterService(service.get())) :
                        specification.and(Specification.where(filterService(service.get())));
            if(from.isPresent() || to.isPresent())
                specification = specification == null ? Specification.where(filterDate(from.orElse(null), to.orElse(null))) :
                        specification.and(Specification.where(filterDate(from.orElse(null), to.orElse(null))));

            Page<Log> logs = logRepository.findAll(specification, paging);
            PagedDAO<LogDAO> pagedDAO = new PagedDAO<>();
            pagedDAO.setTotal(logs.getTotalElements());
            pagedDAO.setData(logs.stream().map(LogDAO::create).collect(Collectors.toList()));
            return pagedDAO;
        } else {
            Page<Log> logs = logRepository.findAll(paging);
            PagedDAO<LogDAO> pagedDAO = new PagedDAO<>();
            pagedDAO.setTotal(logs.getTotalElements());
            pagedDAO.setData(logs.stream().map(LogDAO::create).collect(Collectors.toList()));
            return pagedDAO;
        }
    }

    public LogDAO create(LogDAO logDAO, Optional<Boolean> createNotification){
        Log log = logRepository.save(logDAO.mapToEntity());
        if(createNotification.orElse(true) && log.getSeverity().equals(LogSeverity.error)){
            NotificationDefinition nd = new NotificationDefinition();
            nd.setCode("LOG_ERROR");
            nd.setMessage(logDAO.getTitle());
            nd.setType(NotificationType.error);
            notificationDefinitionRepository.save(nd);

            NotificationSchedule ns = new NotificationSchedule();
            ns.setDateFrom(LocalDateTime.now());
            ns.setDateTo(LocalDateTime.now().plusMinutes(15));
            ns.setNotificationTimestamp(logDAO.getTimestamp().toLocalDateTime());
            notificationScheduleRepository.save(ns);
        }
        return LogDAO.create(log);
    }

    public void delete(Long id){
        if(!logRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        logRepository.deleteById(id);
    }

    private Specification<Log> filterSeverity(List<LogSeverity> severity) {
        return (root, query, criteriaBuilder) -> root.get("severity").in(severity);
    }

    private Specification<Log> filterService(List<LogService> service) {
        return (root, query, criteriaBuilder) -> root.get("service").in(service);
    }

    private Specification<Log> filterDate(String fromS, String toS) {
        ZonedDateTime from = fromS == null ? null : ZonedDateTime.parse(fromS);
        ZonedDateTime to = toS == null ? null : ZonedDateTime.parse(toS);


        if(from != null && to != null)
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("timestamp"), from, to);
        else if(from != null)
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), from);
        else
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), to);
    }
}
