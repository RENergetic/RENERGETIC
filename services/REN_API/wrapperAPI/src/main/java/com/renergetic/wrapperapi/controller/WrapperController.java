package com.renergetic.wrapperapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.common.dao.WrapperRequestDAO;
import com.renergetic.common.dao.WrapperResponseDAO;
import com.renergetic.wrapperapi.service.WrapperService;
import com.renergetic.wrapperapi.service.LoggedInService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Wrapper Controller", description = "Aggregates data from multiple controller to simplify UI requests")
@RequestMapping("/api/ui")
public class WrapperController {
	
	@Autowired
	private LoggedInService loggedInSv;

	@Autowired
	private WrapperService wrapperSv;
	
	@GetMapping("test")
	public ResponseEntity<Void> test() {
		return ResponseEntity.ok(null);
	}

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/wrapper", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapper(@RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {

        Long userId = loggedInSv.getUser().getId();

        WrapperResponseDAO wrapperResponseDAO = wrapperSv.createWrapperResponse(userId, wrapperRequestBodyDAO);
        return ResponseEntity.ok(wrapperResponseDAO);
    }

    @Operation(summary = "API wrapper for front-end")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/wrapper/{userId}", produces = "application/json")
    public ResponseEntity<WrapperResponseDAO> apiWrapperAdmin(@PathVariable(required = true) Long userId,
                                                              @RequestBody WrapperRequestDAO wrapperRequestBodyDAO) {
        WrapperResponseDAO wrapperResponseDAO = wrapperSv.createWrapperResponse(userId, wrapperRequestBodyDAO);
        return ResponseEntity.ok(wrapperResponseDAO);
    }
}
