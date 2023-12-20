package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public NotFoundException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}
}