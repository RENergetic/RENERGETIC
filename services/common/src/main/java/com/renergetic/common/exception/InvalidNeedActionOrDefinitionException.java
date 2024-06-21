package com.renergetic.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidNeedActionOrDefinitionException extends HttpRuntimeException{

	private static final long serialVersionUID = 1L;

	public InvalidNeedActionOrDefinitionException() {
		super();
	}

	public InvalidNeedActionOrDefinitionException(String formatString, Object... parameters) {
		super(formatString, parameters);
	}

	public InvalidNeedActionOrDefinitionException(String message) {
		super(message);
	}
	
	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}