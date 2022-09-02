package com.renergetic.ingestionapi.exception;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ConnectionException extends BaseException{
	
	private static final long serialVersionUID = -7323289768700997288L;

	public ConnectionException() {
		super ();
	}
	
	public ConnectionException(String message, Object... args) {
		super (message, args);
	}
	
	public ConnectionException(Set<Object> entries, String message, Object... args) {
		super (entries, message, args);
	}
}