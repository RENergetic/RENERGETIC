package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class InvalidCreationIdAlreadyDefinedException extends HttpRuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidCreationIdAlreadyDefinedException() {
		super ();
	}

	public InvalidCreationIdAlreadyDefinedException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidCreationIdAlreadyDefinedException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.CONFLICT;
	}
}
