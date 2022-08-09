package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.renergetic.backdb.dao.NotificationDAO;
import com.renergetic.backdb.repository.NotificationRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;

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

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Notifications")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<NotificationDAO>> getAllNotifications (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){		
		 List<NotificationDAO> notifications = notificationRepository.findAll(new OffSetPaging(offset.orElse(0L), limit.orElse(60)))
				 .stream().map(NotificationDAO::create).collect(Collectors.toList());
		
		return new ResponseEntity<>(notifications, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Notifications by asset id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No notifications found with this name")
	})
	@GetMapping(path = "asset/{asset_id}", produces = "application/json")
	public ResponseEntity<List<NotificationDAO>> getNotificationsByName (@PathVariable Long asset_id){
		List<NotificationDAO> notifications = new ArrayList<>();
		
		notifications = notificationRepository.findByAssetId(asset_id).stream().map(NotificationDAO::create).collect(Collectors.toList());
		
		notifications = notifications.isEmpty() ? null : notifications;
		
		return new ResponseEntity<>(notifications, notifications != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
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
		
		notification = NotificationDAO.create(notificationRepository.findById(id).orElse(null));
		
		return new ResponseEntity<>(notification, notification != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
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
		try {
			notification.setId(null);
			NotificationDAO _notification = NotificationDAO.create(notificationRepository.save(notification.mapToEntity()));
			
			return new ResponseEntity<>(_notification, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
		try {
				if (notificationRepository.existsById(id)) {
					notification.setId(id);
					return new ResponseEntity<>(NotificationDAO.create(notificationRepository.save(notification.mapToEntity())), HttpStatus.OK);
				}else return ResponseEntity.notFound().build();
				
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
		try {
			notificationRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
