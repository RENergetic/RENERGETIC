package com.renergetic.hdrapi.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.DetailsDAO;
import com.renergetic.common.model.details.MeasurementTags;


public class MeasurementTagsDAO extends DetailsDAO{
	// FOREIGN KEY FROM CONNECTION TABLE

	@JsonProperty(value = "measurement_id", required = true)
	Long measurementId;

	public MeasurementTagsDAO(String key, String value, Long measurementId) {
		super(key, value);
		this.measurementId = measurementId;
	}public static MeasurementTagsDAO create(MeasurementTags tag,Long measurementId) {
		MeasurementTagsDAO dao = null;
		if (tag != null) {
			dao = new MeasurementTagsDAO(tag.getKey(), tag.getValue(),measurementId);
		}
		return dao;
	}
}







