package com.renergetic.ingestionapi.dao;

import com.renergetic.common.model.PrimitiveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class FieldRestrictionsDAO {
	String name;
	PrimitiveType type;
	String unit;
	String format;
}
