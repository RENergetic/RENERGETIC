package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidRootNestedException extends HttpRuntimeException{

	private static final long serialVersionUID = 1L;

	public InvalidRootNestedException() {
		super();
	}

	public InvalidRootNestedException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidRootNestedException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}