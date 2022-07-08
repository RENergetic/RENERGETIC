package com.renergetic.backdb.mapper;

import com.renergetic.backdb.dao.*;
import com.renergetic.backdb.model.InformationPanel;
import com.renergetic.backdb.model.InformationTile;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.InformationTileType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InformationTileMapper implements MapperReponseRequest<InformationTile, InformationTileDAOResponse, InformationTileDAORequest> {

    @Override
    public InformationTile toEntity(InformationTileDAORequest dto) {
        if(dto == null)
            return null;
        InformationTile entity = new InformationTile();
//        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLabel(dto.getLabel());
        
        entity.setType(dto.getType());
        
        //entity.setFeatured(dto.getFeatured());
        entity.setLayout(dto.getLayout());
        entity.setProps(dto.getProps());

        InformationPanel infoPanel = new InformationPanel();
        infoPanel.setId(dto.getPanelId());
        entity.setInformationPanel(infoPanel);

        ArrayList<InformationTileMeasurement> informationTileMeasurements = new ArrayList<>();
        for(InformationTileMeasurementDAORequest informationTileMeasurementDAORequest : dto.getInformationTileMeasurements()){
            informationTileMeasurements.add(informationTileMeasurementDAORequest.mapToEntity());
        }
        entity.setInformationTileMeasurements(informationTileMeasurements);

        return entity;
    }

    @Override
    public InformationTileDAOResponse toDTO(InformationTile entity) {
        if(entity == null)
            return null;
        InformationTileDAOResponse dao = new InformationTileDAOResponse();
//        dao.setId(entity.getId());
        dao.setPanelId(entity.getInformationPanel().getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if(entity.getType() != null)
            dao.setType(entity.getType());
       // dao.setFeatured(entity.getFeatured());
        dao.setLayout(entity.getLayout());
        dao.setProps(entity.getProps());

        dao.setMeasurements(entity.getInformationTileMeasurements().stream()
                .map(x -> MeasurementDAOResponse.create(x.getMeasurement(), null)).collect(Collectors.toList()));
        return dao;
    }
}
