package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OptimizerTypeDAO {
	@JsonProperty(required = true, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;

	@JsonProperty(required = true)
	private List<OptimizerParameterDAO> optimizerParameters;
	
	public static OptimizerTypeDAO create(OptimizerType ot) {
		return new OptimizerTypeDAO(ot.getId(), ot.getName(), ot.getOptimizerParameters()
				.stream().map(OptimizerParameterDAO::create).collect(Collectors.toList()));
	}
	
	public OptimizerType mapToEntity() {
		OptimizerType ot = new OptimizerType();
		ot.setId(id);
		ot.setName(name);
		ot.setOptimizerParameters(optimizerParameters.stream().map(x -> x.mapToEntity(ot)).collect(Collectors.toList()));
		return ot;
	}
	
}
