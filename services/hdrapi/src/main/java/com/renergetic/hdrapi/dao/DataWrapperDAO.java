package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DataWrapperDAO {


	@JsonProperty(value = "data" )
	private DataDAO data;


	public DataWrapperDAO(DataDAO dataDAO) {
		this.data=dataDAO;
	}
}