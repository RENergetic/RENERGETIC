package com.renergetic.backdb.exception;

public class InvalidNonExistingIdException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidNonExistingIdException() {
		super ();
	}
	
	public InvalidNonExistingIdException(String message) {
		super (message);
	}
}
