package com.renergetic.ingestionapi.controller;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.renergetic.ingestionapi.exception.BaseException;
import com.renergetic.ingestionapi.exception.ConnectionException;
import com.renergetic.ingestionapi.exception.InvalidArgumentException;
import com.renergetic.ingestionapi.exception.TimeFormatException;
import com.renergetic.ingestionapi.exception.TooLargeRequestException;
import com.renergetic.common.model.Request;
import com.renergetic.common.model.RequestError;
import com.renergetic.ingestionapi.service.LogsService;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
	@Autowired
	private LogsService logs;
	
	@ExceptionHandler( {InvalidArgumentException.class, TimeFormatException.class})
	public ResponseEntity<Request> handleArgumentException(BaseException ex, ServletWebRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(manageBaseException(ex, request, status), status);
	}
	
	@ExceptionHandler( {TooLargeRequestException.class})
	public ResponseEntity<Object> handleTooLargeRequestException(TooLargeRequestException ex, ServletWebRequest request) {
		HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
		return new ResponseEntity<>(manageBaseException(ex, request, status), status);
	}
	
	@ExceptionHandler( {ConnectionException.class})
	public ResponseEntity<Request> handleConnectionException(BaseException ex, ServletWebRequest request) {
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		return new ResponseEntity<>(manageBaseException(ex, request, status), status);
	}
	
	@ExceptionHandler( {UnknownHostException.class} )
	public ResponseEntity<Request> handleUnknowHostException(UnknownHostException ex, ServletWebRequest request) {
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		
		String path = request.getRequest().getRequestURI();
		String method = request.getHttpMethod().toString();
		String origin = request.getHeader(HttpHeaders.ORIGIN);

		Request requestInfo = logs.save(new Request(method, origin != null? origin : "unknown", path, null, status.value()));
		RequestError requestError = logs.save(new RequestError(ex.getClass().getSimpleName(), ex.getMessage(), null, requestInfo));
		requestInfo.getErrors().add(requestError);
		return new ResponseEntity<>(requestInfo, status);
	}
	
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
	
	private Request manageBaseException(BaseException ex, ServletWebRequest request, HttpStatus status) {
		String path = request.getRequest().getRequestURI();
		String method = request.getHttpMethod().toString();
		String origin = request.getHeader(HttpHeaders.ORIGIN);
		
		Request requestInfo = logs.save(new Request(method, origin != null? origin : "unknown", path, null, status.value()));
		List<RequestError> errors = new ArrayList<>();
		
		if (ex.getEntries() != null && ex.getEntries().size() > 0)
			for (Object entry : ex.getEntries())
				 errors.add(new RequestError(ex.getClass().getSimpleName(), ex.getMessage(), entry.toString(), requestInfo));
		else
			errors.add(new RequestError(ex.getClass().getSimpleName(), ex.getMessage(), null, requestInfo));
		logs.save(errors);
		requestInfo.getErrors().addAll(errors);
		
		return requestInfo;
	}
}
