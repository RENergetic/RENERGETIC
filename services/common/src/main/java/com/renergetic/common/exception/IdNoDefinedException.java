package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class IdNoDefinedException extends HttpRuntimeException{

	private static final long serialVersionUID = -3547042448040107613L;

	public IdNoDefinedException() {
		super();
	}

	public IdNoDefinedException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public IdNoDefinedException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NO_CONTENT;
	}
}