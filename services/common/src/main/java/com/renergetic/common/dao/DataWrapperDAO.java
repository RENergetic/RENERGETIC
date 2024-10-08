package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataWrapperDAO {


	@JsonProperty(value = "data" )
	private DataDAO data;
	//TODO: add this here?
//	@JsonProperty(value = "timeseries" )
//	private TimeseriesDAO timeseries;

	@JsonProperty(value = "panel" )
	private InformationPanelDAOResponse panel=null;


	public DataWrapperDAO(DataDAO dataDAO) {
		this.data=dataDAO;
	}
	public DataWrapperDAO(DataDAO dataDAO,InformationPanelDAOResponse panel) {
		this.data=dataDAO;
		this.panel=panel;
	}
}