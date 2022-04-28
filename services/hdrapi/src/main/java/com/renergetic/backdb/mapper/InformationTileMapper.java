package com.renergetic.backdb.mapper;

import com.renergetic.backdb.dao.InformationTileDAORequest;
import com.renergetic.backdb.dao.InformationTileDAOResponse;
import com.renergetic.backdb.dao.MeasurementDAORequest;
import com.renergetic.backdb.dao.MeasurementDAOResponse;
import com.renergetic.backdb.model.InformationPanel;
import com.renergetic.backdb.model.InformationTile;
import com.renergetic.backdb.model.InformationTileType;
import com.renergetic.backdb.service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InformationTileMapper implements MapperReponseRequest<InformationTile, InformationTileDAOResponse, InformationTileDAORequest> {
    @Autowired
    private MeasurementService measurementService;

    @Override
    public InformationTile toEntity(InformationTileDAORequest dto) {
        if(dto == null)
            return null;
        InformationTile entity = new InformationTile();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLabel(dto.getLabel());
        
        InformationTileType tileType = new InformationTileType();
        tileType.setId(dto.getType());
        entity.setType(tileType);
        
        entity.setFeatured(dto.getFeatured());
        entity.setLayout(dto.getLayout());
        entity.setProps(dto.getProps());
        
        InformationPanel infoPanel = new InformationPanel();
        infoPanel.setId(dto.getPanelId());
        entity.setInformationPanel(infoPanel);
        
        if(dto.getMeasurements() != null)
            entity.setMeasurements(dto.getMeasurements().stream().map(MeasurementDAORequest::mapToEntity).collect(Collectors.toList()));
        return entity;
    }

    @Override
    public InformationTileDAOResponse toDTO(InformationTile entity) {
        if(entity == null)
            return null;
        InformationTileDAOResponse dao = new InformationTileDAOResponse();
        dao.setId(entity.getId());
        dao.setPanelId(entity.getInformationPanel().getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if(entity.getType() != null)
            dao.setType(entity.getType().getName());
        dao.setFeatured(entity.getFeatured());
        dao.setLayout(entity.getLayout());
        dao.setProps(entity.getProps());
        if(entity.getMeasurements() != null)
            dao.setMeasurements(entity.getMeasurements().stream()
                    .map(x -> MeasurementDAOResponse.create(x, measurementService.getDetailsByMeasurementId(x.getId())))
                    .collect(Collectors.toList()));
        else
            dao.setMeasurements(new ArrayList<>());
        return dao;
    }
}
