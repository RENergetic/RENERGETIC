package com.renergetic.backdb.exception;

public class InvalidCreationIdAlreadyDefinedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidCreationIdAlreadyDefinedException() {
		super ();
	}
	
	public InvalidCreationIdAlreadyDefinedException(String message) {
		super (message);
	}
}
