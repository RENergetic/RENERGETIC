package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Log;
import com.renergetic.common.model.LogService;
import com.renergetic.common.model.LogSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class LogDAO {
    @JsonProperty(required = false, access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(required = true)
    private ZonedDateTime timestamp;

    @JsonProperty(required = true)
    private LogService service;

    @JsonProperty(required = true)
    private String title;

    @JsonProperty(required = true)
    private String description;

    @JsonProperty(required = true)
    private LogSeverity severity;

    public static LogDAO create(Log log){
        LogDAO logDAO = new LogDAO();
        logDAO.setId(log.getId());
        logDAO.setDescription(log.getDescription());
        logDAO.setTitle(logDAO.getTitle());
        logDAO.setService(log.getService());
        logDAO.setTimestamp(log.getTimestamp());
        logDAO.setSeverity(log.getSeverity());
        return logDAO;
    }

    public Log mapToEntity(){
        Log log = new Log();
        log.setId(id);
        log.setDescription(description);
        log.setTitle(title);
        log.setService(service);
        log.setTimestamp(timestamp);
        log.setSeverity(severity);
        return log;
    }
}
