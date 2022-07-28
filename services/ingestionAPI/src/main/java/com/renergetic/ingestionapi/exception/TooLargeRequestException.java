package com.renergetic.ingestionapi.exception;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
public class TooLargeRequestException extends BaseException{
	
	private static final long serialVersionUID = -7323289768700997288L;

	public TooLargeRequestException() {
		super ();
	}
	
	public TooLargeRequestException(String message, Object... args) {
		super (message, args);
	}
	
	public TooLargeRequestException(Set<Object> entries, String message, Object... args) {
		super (entries, message, args);
	}
}