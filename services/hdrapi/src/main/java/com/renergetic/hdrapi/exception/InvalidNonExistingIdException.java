package com.renergetic.hdrapi.exception;

public class InvalidNonExistingIdException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidNonExistingIdException() {
		super ();
	}
	
	public InvalidNonExistingIdException(String message) {
		super (message);
	}
}
