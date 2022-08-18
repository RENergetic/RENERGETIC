package com.renergetic.ingestionapi.dao;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestInfo<T> {
	private Long inserted;
	private List<T> errors;		
}