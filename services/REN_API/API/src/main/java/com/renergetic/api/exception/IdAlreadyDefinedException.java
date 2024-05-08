package com.renergetic.api.exception;

import org.springframework.http.HttpStatus;

public class IdAlreadyDefinedException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public IdAlreadyDefinedException() {
		super();
	}

	public IdAlreadyDefinedException(String formatString, Object... paramenters) {
		super(formatString, paramenters);
	}

	public IdAlreadyDefinedException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.CONFLICT;
	}
}