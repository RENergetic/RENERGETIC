package com.renergetic.ingestionapi.dao;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RestrictionsDAO {
	Integer requestSize;
	List<String> measurements;
	Map<String, String> tags;
	List<FieldRestrictionsDAO> fields;
}
