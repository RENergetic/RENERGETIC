package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.HDRRequest;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

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
    private Long timestamp;
    @JsonProperty(value = "date_from", required = true)
    private Long dateFrom;
    @JsonProperty(value = "date_to", required = true)
    private Long dateTo;
    @JsonProperty(value = "max_value")
    private Double maxValue;
    @JsonProperty(value = "value_change")
    private Double valueChange;
    @JsonProperty(value = "value_type", required = false)
    private MeasurementTypeDAO valueType;
    @JsonProperty(value = "asset", required = false)
    private SimpleAssetDAO asset;
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
            dao.setValueType(MeasurementTypeDAO.create(request.getValueType()));
            if (request.getAsset() != null) {
                dao.setAsset(SimpleAssetDAO.create(request.getAsset()));
            }
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
        request.setValueType(this.getValueType().mapToEntity());
        request.setJsonConfig(Json.toJson(this.config));
        if (this.getAsset() != null) {
            request.setAsset(this.getAsset().mapToEntity());
        }
        return request;
    }
}
