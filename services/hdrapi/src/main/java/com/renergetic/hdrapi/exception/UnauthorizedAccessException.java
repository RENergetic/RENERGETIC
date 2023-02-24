package com.renergetic.hdrapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedAccessException() {
		super ();
	}
	
	public UnauthorizedAccessException(String message) {
		super (message);
	}
}