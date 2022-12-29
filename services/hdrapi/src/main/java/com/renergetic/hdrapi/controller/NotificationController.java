package com.renergetic.hdrapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.renergetic.hdrapi.dao.NotificationDAO;
import com.renergetic.hdrapi.model.NotificationMessages;
import com.renergetic.hdrapi.repository.NotificationRepository;
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
	NotificationRepository notificationRepository;
	
	@Autowired
	NotificationService notificationSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Notifications")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<NotificationDAO>> getAllNotifications (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		 List<NotificationDAO> notifications = notificationSv.get(offset.orElse(0L), limit.orElse(60));

		return new ResponseEntity<>(notifications, HttpStatus.OK);
	}

	@Operation(summary = "Get Notifications by asset id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No notifications found with related with that asset")
	})
	@GetMapping(path = "asset/{asset_id}", produces = "application/json")
	public ResponseEntity<List<NotificationDAO>> getNotificationsByAssetId (@PathVariable Long asset_id){
		List<NotificationDAO> notifications = new ArrayList<>();
		
		notifications = notificationSv.getByAssetId(asset_id);
		
		return new ResponseEntity<>(notifications, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Notification by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No notifications found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<NotificationDAO> getNotificationsById (@PathVariable Long id){
		NotificationDAO notification = null;
		
		notification = notificationSv.getById(id);
		
		return new ResponseEntity<>(notification, HttpStatus.OK);
	}
	
	@Operation(summary = "Shown a list of available notification messages")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly")
	})
	@GetMapping(path = "messages", produces = "application/json")
	public ResponseEntity<List<String>> getNotificationsById (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<String> messages;
		
		messages = notificationSv.getMesagges(offset.orElse(0L), limit.orElse(60));
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Notification")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Notification saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving notification")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<NotificationDAO> createNotification(@RequestBody NotificationDAO notification) {
		notification.setId(null);
		NotificationDAO _notification = notificationSv.save(notification);
		
		return new ResponseEntity<>(_notification, HttpStatus.CREATED);
	}
	
	@Operation(summary = "Create a new allowed notification message")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly, return list of messages")
	})
	@PostMapping(path = "message", produces = "application/json")
	public ResponseEntity<List<String>> createMessage (@RequestBody NotificationMessages message){
		notificationSv.saveMessage(message.getMessage());
		
		return new ResponseEntity<>(notificationSv.getMesagges(0L, 60), HttpStatus.OK);
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing Notification")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Notification saved correctly"),
			@ApiResponse(responseCode = "404", description = "Notification not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving notification")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<NotificationDAO> updateNotification(@RequestBody NotificationDAO notification, @PathVariable Long id) {
		return new ResponseEntity<>(notificationSv.update(notification), HttpStatus.OK);
	}

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
	@DeleteMapping(path = "message")
	public ResponseEntity<?> deleteMessage(@RequestBody NotificationMessages message) {
	notificationSv.deleteMessage(message.getMessage());
	
	return ResponseEntity.noContent().build();
	}
}
