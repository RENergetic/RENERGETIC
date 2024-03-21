package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.LogDAO;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.model.Log;
import com.renergetic.common.model.LogService;
import com.renergetic.common.model.LogSeverity;
import com.renergetic.common.repository.LogRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {
    @Autowired
    private LogRepository logRepository;

    public List<LogDAO> list(Optional<Long> offset, Optional<Integer> limit, Optional<String> orderBy,
                             Optional<LogSeverity> severity, Optional<LogService> service,
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

            return logRepository.findAll(specification, paging).stream().map(LogDAO::create).collect(Collectors.toList());
        } else {
            return logRepository.findAll(paging).stream().map(LogDAO::create).collect(Collectors.toList());
        }
    }

    public LogDAO create(LogDAO logDAO){
        return LogDAO.create(logRepository.save(logDAO.mapToEntity()));
    }

    public void delete(Long id){
        if(!logRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        logRepository.deleteById(id);
    }

    private Specification<Log> filterSeverity(LogSeverity severity) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("severity"), severity);
    }

    private Specification<Log> filterService(LogService service) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("service"), service);
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
