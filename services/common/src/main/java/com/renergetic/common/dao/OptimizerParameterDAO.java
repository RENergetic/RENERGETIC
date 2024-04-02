package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.OptimizerParameter;
import com.renergetic.common.model.OptimizerType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OptimizerParameterDAO {
	@JsonProperty(required = true, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String parameterName;

	@JsonProperty(required = true)
	private Boolean required;
	
	public static OptimizerParameterDAO create(OptimizerParameter op) {
		return new OptimizerParameterDAO(op.getId(), op.getParameterName(), op.isRequired());
	}
	
	public OptimizerParameter mapToEntity(OptimizerType optimizerType) {
		OptimizerParameter op = new OptimizerParameter();
		op.setId(id);
		op.setParameterName(parameterName);
		op.setRequired(required);
		op.setOptimizerType(optimizerType);
		return op;
	}
	
}
