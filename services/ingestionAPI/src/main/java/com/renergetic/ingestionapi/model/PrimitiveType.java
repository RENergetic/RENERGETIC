package com.renergetic.ingestionapi.model;

public enum PrimitiveType {
	INTEGER ("^-?\\d+$"),
	DOUBLE ("^-?\\d+(.\\d+)?$"),
	UNSIGNED_INTEGER ("^\\d+$"),
	UNSIGNED_DOUBLE ("^\\d+(.\\d+)?$"),
	BOOLEAN ("(true) | (false)"),
	STRING (".*");
	
	private String check;
	
	private PrimitiveType(String check) {
		this.check = check;
	}
	
	public Boolean validate(String value) {
		return value.matches(this.check);
	}
}
