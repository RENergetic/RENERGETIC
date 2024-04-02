package com.renergetic.common.mapper;

import com.renergetic.common.dao.InformationPanelDAORequest;
import com.renergetic.common.dao.InformationPanelDAOResponse;
import com.renergetic.common.dao.InformationTileDAOResponse;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.model.InformationTile;
import com.renergetic.common.model.InformationTileMeasurement;
import com.renergetic.common.utilities.Json;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class InformationPanelMapper {
    public InformationPanel toEntity(InformationPanelDAORequest dto) {
        if (dto == null)
            return null;
        
        InformationPanel entity =  new InformationPanel();
        entity.setId(dto.getId());

        entity.setLabel(dto.getLabel());
        entity.setName(dto.getName());

        return entity;
    }

    private InformationTileDAOResponse tileToDTO(InformationTile entity, Boolean includeMeasurements) {
        try {
            if (entity == null)
                return null;
            InformationTileDAOResponse dao = new InformationTileDAOResponse();
            dao.setId(entity.getId());
//            if (entity.getInformationPanel() != null)
//            dao.setPanel(InformationPanelDAOResponse.create(entity.getInformationPanel()));
            if (includeMeasurements) {
                dao.setMeasurements(
                        entity.getInformationTileMeasurements().stream()
                                .map(InformationTileMeasurement::getMeasurementDAO)
                                .collect(Collectors.toList()));
            }
            dao.setName(entity.getName());
            dao.setLabel(entity.getLabel());
            if (entity.getType() != null)
                dao.setType(entity.getType());
            dao.setLayout(Json.parse(entity.getLayout()).toMap());
            dao.setProps(Json.parse(entity.getProps()).toMap());
            return dao;
        } catch (ParseException e) {
            e.printStackTrace();
            //todo: handle error
            return null;
        }
    }

    public InformationPanelDAOResponse toDTO(InformationPanel entity) {
        return toDTO(entity, false);
    }

    public InformationPanelDAOResponse toDTO(InformationPanel entity, Boolean detailed) {
        if (entity == null)
            return null;
        InformationPanelDAOResponse dao = new InformationPanelDAOResponse();
        dao.setId(entity.getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        dao.setFeatured(entity.getFeatured());
        dao.setIsTemplate(entity.getIsTemplate());
        if (entity.getTiles() != null)
            dao.setTiles(
                    entity.getTiles().stream().map(x -> tileToDTO(x, detailed)).collect(Collectors.toList()));
        else
            dao.setTiles(new ArrayList<>());
        if (entity.getProps() != null && !entity.getProps().isBlank()) {
            try {
                dao.setProps(Json.parse(entity.getProps()).toMap());
            } catch (ParseException e) {
                dao.setProps(Collections.emptyMap());
            }
        }
        return dao;
    }
}
