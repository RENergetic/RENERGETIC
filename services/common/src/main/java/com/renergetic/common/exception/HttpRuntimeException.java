package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class HttpRuntimeException extends RuntimeException{
	
	private static final long serialVersionUID = 3441329874180615798L;

	public HttpRuntimeException() {
		super ();
	}
	
	public HttpRuntimeException(String message) {
		super (message);
	}
	
	public HttpRuntimeException(String formatString, Object...parameters) {
		super (String.format(formatString, parameters));
	}

	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
