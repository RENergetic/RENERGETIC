package com.renergetic.ingestionapi.exception;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends BaseException{
	
	private static final long serialVersionUID = -7323289768700997288L;

	public InvalidArgumentException() {
		super ();
	}
	
	public InvalidArgumentException(String message, Object... args) {
		super (message, args);
	}
	
	public InvalidArgumentException(Set<Object> entries, String message, Object... args) {
		super (entries, message, args);
	}
}