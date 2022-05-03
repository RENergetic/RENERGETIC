package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.InformationTileDAORequest;
import com.renergetic.backdb.dao.InformationTileDAOResponse;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.mapper.InformationTileMapper;
import com.renergetic.backdb.model.InformationPanel;
import com.renergetic.backdb.model.InformationTile;
import com.renergetic.backdb.repository.InformationPanelRepository;
import com.renergetic.backdb.repository.InformationTileRepository;
import com.renergetic.backdb.repository.InformationTileTypeRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformationTileService {
    @Autowired
    private InformationTileMapper informationTileMapper;
    @Autowired
    private InformationTileRepository informationTileRepository;
    @Autowired
    private InformationPanelRepository informationPanelRepository;
    @Autowired
    private InformationTileTypeRepository informationTileTypeRepository;
    
    public List<InformationTileDAOResponse> getAllByPanelId(Long panelId, long offset, int limit){
        return informationTileRepository.findAllByInformationPanelId(new OffSetPaging(offset, limit), panelId).stream().map(x -> informationTileMapper.toDTO(x)).collect(Collectors.toList());
    }

    public InformationTileDAOResponse getById(Long id){
        return informationTileMapper.toDTO(informationTileRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public InformationTileDAOResponse getByName(String name){
        return informationTileMapper.toDTO(informationTileRepository.findByName(name).orElseThrow(NotFoundException::new));
    }

    public InformationTileDAOResponse update(InformationTileDAORequest informationTile){
        if(informationTile.getId() == null || !informationTileRepository.existsById(informationTile.getId()))
            throw new InvalidNonExistingIdException();
        if (!informationTileTypeRepository.existsById(informationTile.getType()))
            throw new InvalidNonExistingIdException();

        return informationTileMapper.toDTO(informationTileRepository.save(informationTileMapper.toEntity(informationTile)));
    }

    public InformationTileDAOResponse save(Long informationPanelId, InformationTileDAORequest informationTile) {
        informationTile.setId(null);
        if (!informationTileTypeRepository.existsById(informationTile.getType()))
            throw new InvalidNonExistingIdException();
        InformationPanel informationPanel = informationPanelRepository.findById(informationPanelId).orElseThrow(InvalidNonExistingIdException::new);
        InformationTile entity = informationTileMapper.toEntity(informationTile);
        informationTileRepository.save(entity);
        informationPanel.getTiles().add(entity);
        informationPanelRepository.save(informationPanel);

        return informationTileMapper.toDTO(entity);
    }

    public boolean delete(Long id){
        if(id == null || !informationTileRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        informationTileRepository.deleteById(id);
        return true;
    }
}
