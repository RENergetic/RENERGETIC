package com.renergetic.ingestionapi.exception;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseException extends RuntimeException{

	private static final long serialVersionUID = 7857969934182260554L;
	
	private Set<Object> entries;
	
	public BaseException() {
		super();
		
		entries = null;
	}
	
	public BaseException(String message, Object... args) {
		super(String.format(message, args));
		
		entries = null;
	}
	
	public BaseException(Set<Object> entries, String message, Object... args) {
		super(String.format(message, args));

		this.entries = entries;
	}
	
}
