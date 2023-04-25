package com.renergetic.hdrapi.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.renergetic.hdrapi.service.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.hdrapi.dao.NotificationDefinitionDAO;
import com.renergetic.hdrapi.dao.NotificationScheduleDAO;
import com.renergetic.hdrapi.repository.NotificationScheduleRepository;
import com.renergetic.hdrapi.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Notification Controller", description = "Allows add and see Notifications")
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    NotificationScheduleRepository notificationRepository;

    @Autowired
    NotificationService notificationSv;

//=== GET REQUESTS ====================================================================================

    @Operation(summary = "Get All Notifications")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<NotificationScheduleDAO>> getAllNotifications(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(name = "show_expired", required = false) Optional<Boolean> showExpired) {
        List<NotificationScheduleDAO> notifications =
                notificationSv.get(offset.orElse(0L), limit.orElse(60), showExpired.orElse(false));

        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "Get Notifications by asset id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No notifications found with related with that asset")
    })
    @GetMapping(path = "asset/{asset_id}", produces = "application/json")
    public ResponseEntity<List<NotificationScheduleDAO>> getNotificationsByAssetId(
            @PathVariable Long asset_id,
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(name = "show_expired", required = false) Optional<Boolean> showExpired) {
        List<NotificationScheduleDAO> notifications = new ArrayList<>();

        notifications =
                notificationSv.getByAssetId(asset_id, offset.orElse(0L), limit.orElse(60), showExpired.orElse(false));

        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "Get Notification by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "400", description = "Malformed URL"),
            @ApiResponse(responseCode = "404", description = "No notifications found with this id")
    })
    @GetMapping(path = "{id}", produces = "application/json")
    public ResponseEntity<NotificationScheduleDAO> getNotificationsById(@PathVariable Long id) {
        NotificationScheduleDAO notification = null;

        notification = notificationSv.getById(id);

        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @Operation(summary = "Shown a list of available notification messages")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly")
    })
    @GetMapping(path = "definition", produces = "application/json")
    public ResponseEntity<List<NotificationDefinitionDAO>> getNotificationsDefinition(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit) {
        List<NotificationDefinitionDAO> definitions;

        definitions = notificationSv.getDefinition(offset.orElse(0L), limit.orElse(60));

        return new ResponseEntity<>(definitions, HttpStatus.OK);
    }

//=== POST REQUESTS ===================================================================================

    @Operation(summary = "Create a new Notification")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving notification")
    }
    )
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<NotificationScheduleDAO> createNotification(
            @RequestBody NotificationScheduleDAO notification) {
        notification.setId(null);
        long creationDate = (DateConverter.toEpoch(LocalDateTime.now()));
        notification.setNotificationTimestamp(creationDate);
        NotificationScheduleDAO _notification = notificationSv.save(notification);

        return new ResponseEntity<>(_notification, HttpStatus.CREATED);
    }

    @PostMapping(path = "/batch", produces = "application/json", consumes = "application/json")
    public ResponseEntity<List<NotificationScheduleDAO>> notificationBatc(
            @RequestBody List<NotificationScheduleDAO> notifications) {
        long creationDate = (DateConverter.toEpoch(LocalDateTime.now()));
        notifications.stream().forEach(it -> {
            it.setNotificationTimestamp(creationDate);
            it.setId(null);
        });
        List<NotificationScheduleDAO> res =
                notifications.stream().map(notification -> notificationSv.save(notification)).collect(
                        Collectors.toList());

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new allowed notification message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly, return list of messages")
    })
    @PostMapping(path = "definition", produces = "application/json")
    public ResponseEntity<NotificationDefinitionDAO> createDefinition(
            @RequestBody NotificationDefinitionDAO definition) {
        definition = notificationSv.saveDefinition(definition);
        return new ResponseEntity<>(definition, HttpStatus.OK);
    }

//=== PUT REQUESTS ====================================================================================

    //I think we don't need or shouldnt give update notification API - either we delete or just make invalidate method - so the user isn't confused- TOMEK
//	@Operation(summary = "Update a existing Notification")
//	@ApiResponses({
//			@ApiResponse(responseCode = "200", description = "Notification saved correctly"),
//			@ApiResponse(responseCode = "404", description = "Notification not exist"),
//			@ApiResponse(responseCode = "500", description = "Error saving notification")
//		}
//	)
//	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
//	public ResponseEntity<NotificationScheduleDAO> updateNotification(@RequestBody NotificationScheduleDAO notification, @PathVariable Long id) {
//		notification.setId(id);
//		return new ResponseEntity<>(notificationSv.update(notification), HttpStatus.OK);
//	}

//=== DELETE REQUESTS =================================================================================

    @Operation(summary = "Delete a existing Notification")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving notification")
    }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationSv.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a existing Notification")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving notification")
    }
    )
    @DeleteMapping(path = "definition/{id}")
    public ResponseEntity<?> deleteDefinition(@PathVariable("id") Long definition) {
        notificationSv.deleteDefinition(definition);

        return ResponseEntity.noContent().build();
    }
}
