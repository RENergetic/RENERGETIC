package com.renergetic.common.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class MeasurementRelatedDAO {
	String name;
	String label;
	String sensor_name;
	MeasurementDAO data;
	AssetDAORequest asset;
}
