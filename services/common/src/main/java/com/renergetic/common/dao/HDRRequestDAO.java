package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.model.HDRRecommendation;
import com.renergetic.common.model.HDRRequest;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HDRRequestDAO {
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = true)
    private LocalDateTime timestamp;
    @JsonProperty(value = "date_from", required = true)
    private LocalDateTime dateFrom;
    @JsonProperty(value = "date_to", required = true)
    private LocalDateTime dateTo;
    @Column(name = "max_value")
    private Double maxValue;
    @JsonProperty(value = "value_change")
    private Double valueChange;
    @JsonProperty(required = false)
    private MeasurementType valueType;
    @JsonProperty(value = "config")
    private Map<String, Object> config;


    public static HDRRequestDAO create(HDRRequest request) {

        HDRRequestDAO dao = null;


        if (request != null) {
            dao = new HDRRequestDAO();
            dao.setTimestamp(request.getTimestamp());
            dao.setDateFrom(request.getDateFrom());
            dao.setDateTo(request.getDateTo());
            dao.setId(request.getId());
            dao.setMaxValue(request.getMaxValue());
            dao.setValueChange(request.getValueChange());
            dao.setValueType(request.getValueType());
            if (request.getJsonConfig() != null)
                try {
                    dao.setConfig(Json.parse(request.getJsonConfig()).toMap());
                } catch (ParseException e) {
                    //tODO: verify catch
                    dao.setConfig(new HashMap<>());
                }

        }
        return dao;
    }

    public HDRRequest mapToEntity() {
        HDRRequest request = new HDRRequest();
        request.setId(this.getId());
        request.setTimestamp(this.getTimestamp());
        request.setDateFrom(this.getDateFrom());
        request.setDateTo(this.getDateTo());
        request.setId(this.getId());
        request.setMaxValue(this.getMaxValue());
        request.setValueChange(this.getValueChange());
        request.setValueType(this.getValueType());
        request.setJsonConfig(Json.toJson(this.config));
        return request;
    }
}
