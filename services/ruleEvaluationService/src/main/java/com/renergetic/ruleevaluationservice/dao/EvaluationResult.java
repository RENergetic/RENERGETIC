package com.renergetic.ruleevaluationservice.dao;

import com.renergetic.common.model.NotificationSchedule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationResult {
    private MeasurementSimplifiedDAO dataResponseMeasurement1;
    private MeasurementSimplifiedDAO dataResponseMeasurement2;
    private String executedReadableString;
    private String executedString;
    private String executionResult;
    private String errorMessage;
    private NotificationSchedule notificationSchedule;
}
