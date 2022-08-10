package com.renergetic.ingestionapi.exception;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TimeFormatException extends BaseException{
	
	private static final long serialVersionUID = -7323289768700997288L;

	public TimeFormatException() {
		super ();
	}
	
	public TimeFormatException(String message, Object... args) {
		super (message, args);
	}
	
	public TimeFormatException(Set<Object> entries, String message, Object... args) {
		super (entries, message, args);
	}
}