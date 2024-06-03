package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTimeFormatException extends HttpRuntimeException{

	private static final long serialVersionUID = 1L;

	public InvalidTimeFormatException() {
		super();
	}

	public InvalidTimeFormatException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidTimeFormatException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}