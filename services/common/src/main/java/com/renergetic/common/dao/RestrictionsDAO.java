package com.renergetic.common.dao;

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
	Map<String, String> tags;
	List<FieldRestrictionsDAO> fields;
}
