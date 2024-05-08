package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidNonExistingIdException extends HttpRuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidNonExistingIdException() {
		super ();
	}

	public InvalidNonExistingIdException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidNonExistingIdException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}
}
