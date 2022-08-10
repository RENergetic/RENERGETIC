package com.renergetic.ingestionapi.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeasurementRelatedDAO {
	String name;
	String label;
	String sensor_name;
	MeasurementDAO data;
	AssetDAO asset;
}
