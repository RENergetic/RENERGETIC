package com.renergetic.kpiapi.exception;

import org.springframework.http.HttpStatus;

public class IdAlreadyDefinedException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public IdAlreadyDefinedException() {
		super();
	}

	public IdAlreadyDefinedException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public IdAlreadyDefinedException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.CONFLICT;
	}
}