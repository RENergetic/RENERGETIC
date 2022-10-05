package com.renergetic.hdrapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidArgumentException() {
		super ();
	}
	
	public InvalidArgumentException(String message) {
		super (message);
	}
}