package com.renergetic.common.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestInfo<T> {
	private Long inserted;
	private List<T> errors;		
}