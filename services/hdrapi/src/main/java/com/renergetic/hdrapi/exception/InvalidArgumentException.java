package com.renergetic.hdrapi.exception;

public class InvalidArgumentException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidArgumentException() {
		super ();
	}
	
	public InvalidArgumentException(String message) {
		super (message);
	}
}