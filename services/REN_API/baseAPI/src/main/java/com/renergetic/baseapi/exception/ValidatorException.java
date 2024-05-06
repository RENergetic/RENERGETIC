package com.renergetic.baseapi.exception;

import org.springframework.http.HttpStatus;

import com.renergetic.common.exception.HttpRuntimeException;

public class ValidatorException extends HttpRuntimeException {
    ValidatorException(String message){
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
       return HttpStatus.BAD_REQUEST;
    }
}
