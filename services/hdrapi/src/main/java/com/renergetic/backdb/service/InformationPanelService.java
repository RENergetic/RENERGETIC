package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.*;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.mapper.InformationPanelMapper;
import com.renergetic.backdb.model.InformationPanel;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.UUID;
import com.renergetic.backdb.repository.InformationPanelRepository;
import com.renergetic.backdb.repository.MeasurementRepository;
import com.renergetic.backdb.repository.UuidRepository;
import com.renergetic.backdb.repository.information.MeasurementDetailsRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformationPanelService {
    @Autowired
    InformationPanelRepository informationPanelRepository;

    @Autowired
    InformationPanelMapper informationPanelMapper;
	@Autowired
	UuidRepository uuidRepository;

	@Autowired
    MeasurementDetailsRepository measurementDetailsRepository;

	@Autowired
    MeasurementRepository measurementRepository;

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
        InformationPanel infoPanelEntity = informationPanelMapper.toEntity(informationPanel);
        infoPanelEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        return informationPanelMapper.toDTO(informationPanelRepository.save(infoPanelEntity));
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

    public List<InformationPanelDAOResponse> findByUserId(Long id, long offset, int limit){
        /*
            Highly inefficient !!
            Just as a first draft to get it working but there is really a structural issue in the data highlighted here as this is a pure mess to get the request.
        */
        List<InformationPanel> informationPanels = informationPanelRepository.findByUserId(id, offset, limit);
        return informationPanels.stream().map(panel -> InformationPanelDAOResponse.create(panel,
                panel.getTiles().stream().map(tile -> InformationTileDAOResponse.create(tile,
                        tile.getInformationTileMeasurements().stream().map(tileM -> tileM.getMeasurement() == null ?
                                getMeasurementInferredFromTile(id, tileM)
                                : getMeasurementFromTileMeasurement(tileM)).flatMap(List::stream).collect(Collectors.toList())
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    private List<MeasurementDAOResponse> getMeasurementInferredFromTile(Long userId, InformationTileMeasurement tileM){
        return measurementRepository.findByUserIdAndBySensorNameAndDomainAndDirectionAndType(userId, tileM.getSensorName(), tileM.getDomain(), tileM.getDirection(), tileM.getType())
                .stream().map(x -> MeasurementDAOResponse.create(x, measurementDetailsRepository.findByMeasurementId(x.getId()))).collect(Collectors.toList());
    }

    private List<MeasurementDAOResponse> getMeasurementFromTileMeasurement(InformationTileMeasurement tileM){
        return Collections.singletonList(MeasurementDAOResponse.create(tileM.getMeasurement(), measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId())));
    }
}
