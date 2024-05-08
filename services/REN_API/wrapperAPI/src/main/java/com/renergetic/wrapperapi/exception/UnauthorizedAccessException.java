package com.renergetic.wrapperapi.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public UnauthorizedAccessException() {
		super();
	}

	public UnauthorizedAccessException(String formatString, Object... paramenters) {
		super(formatString, paramenters);
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.FORBIDDEN;
	}
}