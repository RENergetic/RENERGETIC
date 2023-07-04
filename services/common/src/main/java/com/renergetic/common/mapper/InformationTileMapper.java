package com.renergetic.common.mapper;

import com.renergetic.common.dao.InformationTileDAORequest;
import com.renergetic.common.dao.InformationTileDAOResponse;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.model.InformationTile;
import com.renergetic.common.utilities.Json;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

@Service
public class InformationTileMapper implements MapperReponseRequest<InformationTile, InformationTileDAOResponse, InformationTileDAORequest> {

    @Override
    public InformationTile toEntity(InformationTileDAORequest dto) {
        if (dto == null)
            return null;
        InformationTile entity = new InformationTile();
        entity.setName(dto.getName());
        entity.setLabel(dto.getLabel());

        entity.setType(dto.getType());

        entity.setLayout(dto.getLayout());
        entity.setProps(dto.getProps());
        if(dto.getPanelId()!=null) {
            InformationPanel infoPanel = new InformationPanel();
            infoPanel.setId(dto.getPanelId());
            entity.setInformationPanel(infoPanel);
        }

        return entity;
    }

    @Override
    public InformationTileDAOResponse toDTO(InformationTile entity) {
        try {
            if (entity == null)
                return null;
            InformationTileDAOResponse dao = new InformationTileDAOResponse();
            dao.setId(entity.getId());
            if(entity.getInformationPanel()!=null)
//            dao.setPanel(InformationPanelDAOResponse.create(entity.getInformationPanel()));

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
}
