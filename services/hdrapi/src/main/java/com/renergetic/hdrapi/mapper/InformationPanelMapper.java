package com.renergetic.hdrapi.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.InformationPanelDAORequest;
import com.renergetic.hdrapi.dao.InformationPanelDAOResponse;
import com.renergetic.hdrapi.model.InformationPanel;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InformationPanelMapper implements MapperReponseRequest<InformationPanel, InformationPanelDAOResponse, InformationPanelDAORequest> {
    @Autowired
    private InformationTileMapper informationTileMapper;

    @Override
    public InformationPanel toEntity(InformationPanelDAORequest dto) {
        if(dto == null)
            return null;
        InformationPanel entity = new InformationPanel();
        entity.setId(dto.getId());
        entity.setLabel(dto.getLabel());
        entity.setName(dto.getName());
       // entity.setUuid(new UUID(dto.getUuid()));
//        if(dto.getOwner_id() != null){
//            User user = new User();
//            user.setId(dto.getOwner_id());
//        @GeneratedValue(strategy = GenerationType.AUTO)
        return entity;
    }

    @Override
    public InformationPanelDAOResponse toDTO(InformationPanel entity) {
        if(entity == null)
            return null;
        InformationPanelDAOResponse dao = new InformationPanelDAOResponse();
        dao.setId(entity.getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        if(entity.getTiles() != null)
            dao.setTiles(entity.getTiles().stream().map(x -> informationTileMapper.toDTO(x)).collect(Collectors.toList()));
        else
            dao.setTiles(new ArrayList<>());
        return dao;
    }
}
