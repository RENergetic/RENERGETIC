package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.LogDAO;
import com.renergetic.common.dao.wrapper.PagedDAO;
import com.renergetic.common.model.LogService;
import com.renergetic.common.model.LogSeverity;
import com.renergetic.hdrapi.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Log Controller", description = "Allows to create log entries or list them")
@RequestMapping("/api/log")
public class LogController {
    @Autowired
    private LoggingService loggingService;

    @Operation(summary = "Get All logs entries with or without filters")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/list", produces = "application/json")
    public ResponseEntity<PagedDAO<LogDAO>> listLogs(@RequestParam(name = "offset", required = false) Optional<Long> offset,
                                                     @RequestParam(name = "limit", required = false) Optional<Integer> limit,
                                                     @RequestParam(name = "orderBy", required = false) Optional<String> orderBy,
                                                     @RequestParam(name = "severity", required = false) Optional<List<LogSeverity>> severity,
                                                     @RequestParam(name = "service", required = false) Optional<List<LogService>> service,
                                                     @RequestParam(name = "from", required = false) Optional<String> from,
                                                     @RequestParam(name = "to", required = false) Optional<String> to){
        return new ResponseEntity<>(loggingService.list(offset, limit, orderBy, severity, service, from, to), HttpStatus.OK);
    }

    @Operation(summary = "Create a new log entry")
    @ApiResponse(responseCode = "201", description = "Request executed correctly")
    @PostMapping(path = "/create", produces = "application/json")
    public ResponseEntity<LogDAO> createLog(@RequestBody LogDAO logDAO,
                                            @RequestParam(name = "notification", required = false) Optional<Boolean> notification){
        return new ResponseEntity<>(loggingService.create(logDAO, notification), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a log entry by id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/delete/{logId}", produces = "application/json")
    public ResponseEntity<LogDAO> deleteLog(@PathVariable(name = "logId") Long logId){
        loggingService.delete(logId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
