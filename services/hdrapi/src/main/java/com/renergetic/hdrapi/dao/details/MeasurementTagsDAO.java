package com.renergetic.hdrapi.dao.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Details;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.details.MeasurementDetails;
import com.renergetic.hdrapi.model.details.MeasurementTags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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







