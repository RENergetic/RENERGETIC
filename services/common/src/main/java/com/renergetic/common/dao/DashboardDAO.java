package com.renergetic.common.dao;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.common.model.Dashboard;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.User;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import jakarta.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DashboardDAO {
    @JsonProperty(required = false )
    private Long id;

    @JsonProperty(required = false)
    private String name;

    @Pattern(regexp = "https?://\\S+([/?].+)?", message = "URL isn't valid format")
    @JsonProperty(required = true)
    private String url;

    @JsonProperty(required = false)
    private String label;

    @JsonProperty(value = "grafana_id", required = false)
    private String grafanaId;

    @JsonProperty(required = false)
    private Map<String, ?> ext;


    @JsonProperty(value = "user_id", required = false)
    private Long userId;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty(access = Access.READ_ONLY, required = false)
    private Integer status;
    
    @JsonProperty(value = "measurement_type", required = false)
    private MeasurementType measurementType;

    public DashboardDAO(String name, String url, String label) {
        super();
        this.name = name;
        this.url = url;
        this.label = label;
    }

    public static DashboardDAO create(Dashboard dashboard) {
        DashboardDAO dao = null;

        if (dashboard != null) {
            dao = new DashboardDAO();

            dao.setId(dashboard.getId());
            dao.setUrl(dashboard.getUrl());
            dao.setName(dashboard.getName());
            dao.setLabel(dashboard.getLabel());
            dao.setGrafanaId(dashboard.getGrafanaId());
            dao.setMeasurementType(dashboard.getMeasurementType());


            if (dashboard.getExt() != null) {
                try {
                    dao.setExt(Json.parse(dashboard.getExt()).toMap());
                } catch (ParseException e) {
                    //tODO: verify catch
                    dao.setExt(new HashMap<>());
                }
            }
            //dao.setLabel(dashboard.getLabel());
            if (dashboard.getUser() != null)
                dao.setUserId(dashboard.getUser().getId());

            if (dashboard.getUser() != null)
                dao.setUserId(dashboard.getUser().getId());
        }
        return dao;
    }

    public Dashboard mapToEntity() {
        Dashboard dashboard = new Dashboard(name, url, label);

        dashboard.setId(id);
        dashboard.setGrafanaId(grafanaId);
        if (this.ext != null)
            dashboard.setExt(Json.toJson(this.ext));

        if (userId != null) {
            User userEntity = new User();
            userEntity.setId(userId);
            dashboard.setUser(userEntity);
        }
        if (measurementType != null) {
            dashboard.setMeasurementType(measurementType);
        }
        return dashboard;
    }
}
