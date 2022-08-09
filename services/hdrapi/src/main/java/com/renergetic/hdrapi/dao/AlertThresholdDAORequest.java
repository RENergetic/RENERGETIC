package com.renergetic.hdrapi.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.AggregationType;
import com.renergetic.hdrapi.model.AlertThreshold;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.ThresholdType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AlertThresholdDAORequest {
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
    
    @JsonProperty(value = "measurement_id", required = true)
    private Long measurementId;
    
    public static AlertThresholdDAORequest create(AlertThreshold alert) {
    	AlertThresholdDAORequest dao = new AlertThresholdDAORequest();
		
		dao.setId(alert.getId());
		dao.setThresholdType(alert.getThresholdType());
		dao.setThresholdConstraint(alert.getThresholdConstraint());
		dao.setThresholdUpdate(alert.getThresholdUpdate());
		
		dao.setAggregationType(alert.getAggregationType());
		dao.setAggregationInterval(alert.getAggregationInterval());
		
		if (alert.getMeasurement() != null)
		dao.setMeasurementId(alert.getMeasurement().getId());

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
		
		if(measurementId != null) {
			Measurement measurement = new Measurement();
			measurement.setId(measurementId);
			alert.setMeasurement(measurement);
		}
		
		return alert;
	}
}
