package com.renergetic.ingestionapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.ingestionapi.dao.RestrictionsDAO;

@Service
public class RestrictionsService {
	@Value("${request.max-size}")
    private Integer maxRequests;
	
	@Autowired
	MeasurementService measurementSv;

	public RestrictionsDAO get() {
		RestrictionsDAO restrictions = new RestrictionsDAO();
		
		restrictions.setRequestSize(maxRequests);
		restrictions.setMeasurements(measurementSv.getMeasurementNames());
		restrictions.setFields(measurementSv.getFieldRestrictions());
		restrictions.setTags(measurementSv.getTagsRestrictions());
		
		
		return restrictions;
	}
}
