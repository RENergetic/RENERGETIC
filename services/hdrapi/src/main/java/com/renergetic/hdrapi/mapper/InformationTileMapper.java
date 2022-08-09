package com.renergetic.hdrapi.mapper;

import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTile;

@Service
public class InformationTileMapper implements MapperReponseRequest<InformationTile, InformationTileDAOResponse, InformationTileDAORequest> {

    @Override
    public InformationTile toEntity(InformationTileDAORequest dto) {
        if(dto == null)
            return null;
        InformationTile entity = new InformationTile();
        entity.setName(dto.getName());
        entity.setLabel(dto.getLabel());
        
        entity.setType(dto.getType());
        
        entity.setLayout(dto.getLayout());
        entity.setProps(dto.getProps());

        InformationPanel infoPanel = new InformationPanel();
        infoPanel.setId(dto.getPanelId());
        entity.setInformationPanel(infoPanel);

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
            dao.setType(entity.getType());
        dao.setLayout(entity.getLayout());
        dao.setProps(entity.getProps());
        return dao;
    }
}
