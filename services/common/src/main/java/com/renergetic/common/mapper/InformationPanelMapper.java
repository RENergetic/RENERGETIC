package com.renergetic.common.mapper;

import com.renergetic.common.dao.InformationPanelDAORequest;
import com.renergetic.common.dao.InformationPanelDAOResponse;
import com.renergetic.common.model.InformationPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InformationPanelMapper implements MapperReponseRequest<InformationPanel, InformationPanelDAOResponse, InformationPanelDAORequest> {
    @Autowired
    private InformationTileMapper informationTileMapper;

    public InformationPanel toEntity(InformationPanelDAORequest dto, InformationPanel entity) {
        if (dto == null)
            return null;
        if(entity==null){
            entity =  new InformationPanel();
            entity.setId(dto.getId());

        }

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
    public InformationPanel toEntity(InformationPanelDAORequest dto ) {
   return this.toEntity(dto,null);
    }
    @Override
    public InformationPanelDAOResponse toDTO(InformationPanel entity) {
        return this.toDTO(entity,false);
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
                    entity.getTiles().stream().map(x -> informationTileMapper.toDTO(x,detailed)).collect(Collectors.toList()));
        else
            dao.setTiles(new ArrayList<>());
        return dao;
    }
}
