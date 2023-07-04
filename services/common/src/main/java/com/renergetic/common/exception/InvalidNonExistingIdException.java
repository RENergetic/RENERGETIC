package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidNonExistingIdException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidNonExistingIdException() {
		super ();
	}
	
	public InvalidNonExistingIdException(String message) {
		super (message);
	}
}
