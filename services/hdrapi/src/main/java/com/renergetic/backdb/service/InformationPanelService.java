package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.InformationPanelDAORequest;
import com.renergetic.backdb.dao.InformationPanelDAOResponse;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.mapper.InformationPanelMapper;
import com.renergetic.backdb.repository.InformationPanelRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformationPanelService {
    @Autowired
    InformationPanelRepository informationPanelRepository;

    @Autowired
    InformationPanelMapper informationPanelMapper;

    public List<InformationPanelDAOResponse> getAll(long offset, int limit){
        return informationPanelRepository.findAll(new OffSetPaging(offset, limit))
                .stream().map(x -> informationPanelMapper.toDTO(x)).collect(Collectors.toList());
    }

    public List<InformationPanelDAOResponse> getAll(Long ownerId){
        return informationPanelRepository.findAllByOwnerId(ownerId).stream().map(x -> informationPanelMapper.toDTO(x)).collect(Collectors.toList());
    }

    public InformationPanelDAOResponse getById(Long id){
        return informationPanelMapper.toDTO(informationPanelRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public InformationPanelDAOResponse getByName(String name){
        return informationPanelMapper.toDTO(informationPanelRepository.findByName(name).orElseThrow(NotFoundException::new));
    }

    public InformationPanelDAOResponse save(InformationPanelDAORequest informationPanel) {
        informationPanel.setId(null);
        return informationPanelMapper.toDTO(informationPanelRepository.save(informationPanelMapper.toEntity(informationPanel)));
    }

    public InformationPanelDAOResponse update(InformationPanelDAORequest informationPanel) {
        if(informationPanel.getId() == null || !informationPanelRepository.existsById(informationPanel.getId()))
            throw new InvalidNonExistingIdException();

        return informationPanelMapper.toDTO(informationPanelRepository.save(informationPanelMapper.toEntity(informationPanel)));
    }

    public boolean deleteById(Long id) {
        if(id == null || !informationPanelRepository.existsById(id))
            throw new NotFoundException();
        informationPanelRepository.deleteById(id);
        return true;
    }
}
