package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.InformationTileType;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.service.utils.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationTileDAOResponse {
    @JsonProperty()
    private Long id;

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
    private Map<String, ?> props;

    @JsonProperty
    private Map<String, ?> layout;

    public static InformationTileDAOResponse create(InformationTile entity) {
        return create(entity, null);
    }

    public static InformationTileDAOResponse create(InformationTile entity, List<MeasurementDAOResponse> measurements) {
        if (entity == null)
            return null;
        InformationTileDAOResponse dao = new InformationTileDAOResponse();
        dao.setId(entity.getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if (entity.getType() != null)
            dao.setType(entity.getType());

        // dao.setFeatured(entity.getFeatured());

        try {
            dao.setLayout(Json.parse(entity.getLayout()).toMap());
        } catch (ParseException e) {
            //tODO: verify catch
            dao.setLayout(null);
        }
        try {
            dao.setProps(Json.parse(entity.getProps()).toMap());
        } catch (ParseException e) {
            //tODO: verify catch
            dao.setProps(new HashMap<>());
        }

        if (measurements == null && entity.getInformationTileMeasurements() != null)
            dao.setMeasurements(
                    entity.getInformationTileMeasurements()
                            .stream()
                            .map(tileM -> {
                                Measurement measurement = tileM.getMeasurement();
                                if (measurement != null)
                                    measurement.setFunction(tileM.getFunction());
                                return MeasurementDAOResponse.create(measurement, null,tileM.getFunction());
                            })
                            .collect(Collectors.toList())
            );
        else dao.setMeasurements(measurements);
        return dao;
    }


}