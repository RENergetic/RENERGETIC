package com.renergetic.ingestionapi.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class RestrictionsDAO {
	Integer requestSize;
	List<String> measurements;
	Map<String, List<String>> tags;
	List<FieldRestrictionsDAO> fields;
}
