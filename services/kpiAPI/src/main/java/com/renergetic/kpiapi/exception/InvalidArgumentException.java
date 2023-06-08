package com.renergetic.kpiapi.exception;

import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public InvalidArgumentException() {
		super();
	}

	public InvalidArgumentException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidArgumentException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}