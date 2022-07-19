package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.backdb.model.InformationTileType;

import com.renergetic.backdb.model.InformationTile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    
    @JsonProperty(value = "panel_id")
    private Long panelId;

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
    private String props;

    @JsonProperty
    private String layout;

    public static InformationTileDAOResponse create(InformationTile entity){
        return create(entity, entity.getInformationTileMeasurements() != null ?
                entity.getInformationTileMeasurements()
                        .stream().map(x -> MeasurementDAOResponse.create(x.getMeasurement(), null)).collect(Collectors.toList()) :
                new ArrayList<>());
    }

    public static InformationTileDAOResponse create(InformationTile entity, List<MeasurementDAOResponse> measurements){
        if(entity == null)
            return null;
        InformationTileDAOResponse dao = new InformationTileDAOResponse();
        dao.setId(entity.getId());
        dao.setPanelId(entity.getInformationPanel().getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if(entity.getType() != null)
            dao.setType(entity.getType());
        // dao.setFeatured(entity.getFeatured());
        dao.setLayout(entity.getLayout());
        dao.setProps(entity.getProps());

        dao.setMeasurements(measurements);
        return dao;
    }
}
