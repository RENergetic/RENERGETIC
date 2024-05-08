package com.renergetic.wrapperapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.renergetic.common.dao.ErrorMessage;
import com.renergetic.common.exception.HttpRuntimeException;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler( HttpRuntimeException.class )
	public ResponseEntity<ErrorMessage> handleArgumentException(HttpRuntimeException ex, ServletWebRequest request) {

		return ErrorMessage.response(ex, request, ex.getHttpStatus());
	}
}
