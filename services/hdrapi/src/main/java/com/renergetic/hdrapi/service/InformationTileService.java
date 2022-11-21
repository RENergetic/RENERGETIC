package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.InformationTileDAORequest;
import com.renergetic.hdrapi.dao.InformationTileDAOResponse;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.mapper.InformationTileMapper;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.repository.InformationPanelRepository;
import com.renergetic.hdrapi.repository.InformationTileRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
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

    public List<InformationTileDAOResponse> getAllByPanelId(Long panelId, long offset, int limit) {
    	List<InformationTileDAOResponse> list = informationTileRepository.findAllByInformationPanelId(new OffSetPaging(offset, limit),
                panelId).stream().map(x -> informationTileMapper.toDTO(x)).collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No tiles related with the panel " + panelId + " found");
    }

    public InformationTileDAOResponse getById(Long id) {
        return informationTileMapper.toDTO(informationTileRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public InformationTileDAOResponse getByName(String name) {
        return informationTileMapper.toDTO(
                informationTileRepository.findByName(name).orElseThrow(NotFoundException::new));
    }

    public InformationTileDAOResponse update(InformationTileDAORequest informationTile) {
        if (informationTile.getId() == null || !informationTileRepository.existsById(informationTile.getId()))
            throw new InvalidNonExistingIdException();

        return informationTileMapper.toDTO(
                informationTileRepository.save(informationTileMapper.toEntity(informationTile)));
    }

    public InformationTileDAOResponse save(Long informationPanelId, InformationTileDAORequest informationTile) {
		if(informationTile.getId() !=  null && informationTileRepository.existsById(informationTile.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a information tile with ID " + informationTile.getId());
		
        InformationTile entity = informationTileMapper.toEntity(informationTile);
        if (informationPanelId != null) {
            InformationPanel informationPanel = informationPanelRepository.findById(informationPanelId).orElseThrow(
                    InvalidNonExistingIdException::new);
            informationPanel.getTiles().add(entity);
            informationPanelRepository.save(informationPanel);
        }
        informationTileRepository.save(entity);

        return informationTileMapper.toDTO(entity);
    }

    public boolean delete(Long id) {
        if (id == null || !informationTileRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        informationTileRepository.deleteById(id);
        return true;
    }
}
