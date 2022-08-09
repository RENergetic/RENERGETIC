package com.renergetic.hdrapi.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.AggregationType;
import com.renergetic.hdrapi.model.AlertThreshold;
import com.renergetic.hdrapi.model.ThresholdType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AlertThresholdDAOResponse {
    @JsonProperty(access = Access.READ_ONLY, required = false)
    private Long id;

    @JsonProperty(value = "threshold_type", required = true)
    private ThresholdType thresholdType;

    @JsonProperty(value = "threshold_constraint", required = true)
    private String thresholdConstraint;

    @JsonProperty(value = "threshold_update", required = true)
    private LocalDateTime thresholdUpdate;
    
    @JsonProperty(value = "aggregation_type", required = false)
    private AggregationType aggregationType;
    
    @JsonProperty(value = "aggregation_interval", required = false)
    private Long aggregationInterval;
    
    @JsonProperty(value = "measurement", required = true)
    private SimpleMeasurementDAO measurement;
    
    public static AlertThresholdDAOResponse create(AlertThreshold alert) {
    	AlertThresholdDAOResponse dao = new AlertThresholdDAOResponse();
		
		dao.setId(alert.getId());
		dao.setThresholdType(alert.getThresholdType());
		dao.setThresholdConstraint(alert.getThresholdConstraint());
		dao.setThresholdUpdate(alert.getThresholdUpdate());
		
		dao.setAggregationType(alert.getAggregationType());
		dao.setAggregationInterval(alert.getAggregationInterval());
		
		if (alert.getMeasurement() != null)
		dao.setMeasurement(SimpleMeasurementDAO.create(alert.getMeasurement()));

		return dao;
	}
	
	public AlertThreshold mapToEntity() {
		AlertThreshold alert = new AlertThreshold();
		
		alert.setId(id);

		alert.setId(id);
		alert.setThresholdType(thresholdType);
		alert.setThresholdConstraint(thresholdConstraint);
		alert.setThresholdUpdate(thresholdUpdate);
		
		alert.setAggregationType(aggregationType);
		alert.setAggregationInterval(aggregationInterval);
		
		if(measurement != null) {
			alert.setMeasurement(measurement.mapToEntity());
		}
		
		return alert;
	}
}
