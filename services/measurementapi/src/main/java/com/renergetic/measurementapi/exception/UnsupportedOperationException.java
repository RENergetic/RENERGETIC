package com.renergetic.measurementapi.exception;

public class UnsupportedOperationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public UnsupportedOperationException() {
		super ();
	}
	
	public UnsupportedOperationException(String message) {
		super (message);
	}
}