package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.InformationTileType;

import com.renergetic.hdrapi.service.utils.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileDAOResponse {
    @JsonProperty()
    private Long id;

    @JsonProperty(value = "panel")
    private InformationPanelDAOResponse panel;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;
    @JsonProperty
    private InformationTileType type;

    @JsonProperty(required = false)
    private Boolean featured;

    @JsonProperty()
    private List<MeasurementDAOResponse> measurements;

    @JsonProperty
    private JSONObject props;

    @JsonProperty
    private JSONObject layout;

    public static InformationTileDAOResponse create(InformationTile entity)  {
        return create(entity, entity.getInformationTileMeasurements() != null ?
                entity.getInformationTileMeasurements()
                        .stream().map(x -> MeasurementDAOResponse.create(x.getMeasurement(), null)).collect(Collectors.toList()) :
                new ArrayList<>());
    }

    public static InformationTileDAOResponse create(InformationTile entity, List<MeasurementDAOResponse> measurements)  {
        if(entity == null)
            return null;
        InformationTileDAOResponse dao = new InformationTileDAOResponse();
        dao.setId(entity.getId());
        dao.setPanel(InformationPanelDAOResponse.create(entity.getInformationPanel()));
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if(entity.getType() != null)
            dao.setType(entity.getType());

        // dao.setFeatured(entity.getFeatured());

        try {
            dao.setLayout(Json.parse(entity.getLayout()));
        } catch (ParseException e) {
            //tODO: verify catch
            dao.setLayout(null);
        }
        try {
            dao.setProps(Json.parse(entity.getProps()));
        } catch (ParseException e) {
            //tODO: verify catch
            dao.setLayout(null);
        }

        dao.setMeasurements(measurements);
        return dao;
    }
}