package com.renergetic.kpiapi.dao;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ErrorMessage {
	Instant timestamp;
	
	String method;
	String path;
	
	String error;
	String message;
	List<String> trace;
	
	public ErrorMessage() {}
	
	public ErrorMessage( RuntimeException exception, ServletWebRequest request ) {
		timestamp = Instant.now();

		path = request.getRequest().getRequestURI();
		method = request.getHttpMethod().name();
		
		error = exception.getClass().getSimpleName();
		message = exception.getMessage();
		trace = List.of(exception.getStackTrace()).stream().map(elem -> {
			String methodName = ("method " + elem.getMethodName() + "()");
			methodName = methodName.substring(0, methodName.length() < 30? methodName.length(): 30);
			return String.format("%-30s: line %04d -> %s"
					, methodName
					, elem.getLineNumber()
					, elem.getFileName());
		}).collect(Collectors.toList()).subList(0, 5);
	}
	
	// GENERATE HTTP RESPONSES
	public ResponseEntity<ErrorMessage> response(HttpStatus status) {
		return new ResponseEntity<>(this, status);
	}
	
	public ResponseEntity<ErrorMessage> response(int status) {
		return new ResponseEntity<>(this, HttpStatus.valueOf(status));
	}
	
	public static ResponseEntity<ErrorMessage> response(RuntimeException exception, ServletWebRequest request, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(exception, request), status);
	}
	
	public static ResponseEntity<ErrorMessage> response(RuntimeException exception, ServletWebRequest request, int status) {
		return new ResponseEntity<>(new ErrorMessage(exception, request), HttpStatus.valueOf(status));
	}
}
