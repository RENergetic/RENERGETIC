package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.InformationTileDAORequest;
import com.renergetic.common.dao.InformationTileDAOResponse;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.mapper.InformationTileMapper;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.model.InformationTile;
import com.renergetic.common.repository.InformationPanelRepository;
import com.renergetic.common.repository.InformationTileRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Deprecated // SEE BASE API
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
