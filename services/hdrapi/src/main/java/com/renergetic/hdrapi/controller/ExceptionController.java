package com.renergetic.hdrapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.renergetic.hdrapi.dao.ErrorMessage;
import com.renergetic.hdrapi.exception.InvalidArgumentException;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler( {InvalidArgumentException.class, InvalidCreationIdAlreadyDefinedException.class, InvalidNonExistingIdException.class, NotFoundException.class} )
	public ResponseEntity<ErrorMessage> handleArgumentException(RuntimeException ex, ServletWebRequest request) {
		HttpStatus status =  HttpStatus.BAD_REQUEST;
		
		if (ex instanceof InvalidCreationIdAlreadyDefinedException)
			status = HttpStatus.CONFLICT;
		else if (ex instanceof InvalidNonExistingIdException || ex instanceof NotFoundException)
			status = HttpStatus.NOT_FOUND;

		return ErrorMessage.response(ex, request, status);
	}
	/*
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String path = ((ServletWebRequest) request).getRequest().getRequestURI();
		String method = ((ServletWebRequest) request).getHttpMethod().toString();

		Request requestInfo = logs.save(new Request(method, headers.getOrigin() != null? headers.getOrigin() : "unknown", path, null, status.value()));
		RequestError requestError = logs.save(new RequestError(ex.getClass().getSimpleName(), ex.getMessage(), null, requestInfo));
		requestInfo.getErrors().add(requestError);
		return new ResponseEntity<>(requestInfo, status);
	}
	*/
}
