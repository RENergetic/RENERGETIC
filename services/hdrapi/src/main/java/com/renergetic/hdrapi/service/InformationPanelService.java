package com.renergetic.hdrapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.InformationPanelDAORequest;
import com.renergetic.hdrapi.dao.InformationPanelDAOResponse;
import com.renergetic.hdrapi.dao.InformationTileDAOResponse;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.mapper.InformationPanelMapper;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTileMeasurement;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.repository.AssetRepository;
import com.renergetic.hdrapi.repository.InformationPanelRepository;
import com.renergetic.hdrapi.repository.MeasurementRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

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
	AssetRepository assetRepository;
	
	@Autowired
    MeasurementRepository measurementRepository;

    public List<InformationPanelDAOResponse> getAll(long offset, int limit){
    	List<InformationPanelDAOResponse> list = informationPanelRepository.findAll(new OffSetPaging(offset, limit))
                .stream().map(x -> informationPanelMapper.toDTO(x)).collect(Collectors.toList());
    	
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No information panels exists");
    }

    public List<InformationPanelDAOResponse> getAll(Long ownerId){
    	List<InformationPanelDAOResponse> list = informationPanelRepository.findAllByOwnerId(ownerId).stream()
    			.map(x -> informationPanelMapper.toDTO(x))
    			.collect(Collectors.toList());
    	
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No information panels related with user " + ownerId + " found");
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
            throw new InvalidNonExistingIdException();
        informationPanelRepository.deleteById(id);
        return true;
    }

    public List<InformationPanelDAOResponse> findByUserId(Long id, long offset, int limit){
        /*
            Highly inefficient !!
            Just as a first draft to get it working but there is really a structural issue in the data highlighted here as this is a pure mess to get the request.
        */
        List<InformationPanel> informationPanels = informationPanelRepository.findByUserId(id, offset, limit);
        List<InformationPanelDAOResponse> list = informationPanels.stream().map(panel -> InformationPanelDAOResponse.create(panel,
                panel.getTiles().stream().map(tile -> InformationTileDAOResponse.create(tile,
                        tile.getInformationTileMeasurements().stream().map(tileM -> tileM.getMeasurement() == null ?
                                getMeasurementInferredFromTile(id, tileM)
                                : getMeasurementFromTileMeasurement(tileM)).flatMap(List::stream).collect(Collectors.toList())
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    	
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No information panels related with user " + id + " found");
    }

    private List<MeasurementDAOResponse> getMeasurementInferredFromTile(Long userId, InformationTileMeasurement tileM){
    	List<MeasurementDAOResponse> list = measurementRepository
                .findByUserIdAndBySensorNameAndDomainAndDirectionAndType(userId, tileM.getSensorName(),
                        tileM.getDomain().name(), tileM.getDirection().name(), tileM.getType().getId())
                .stream().map(x -> MeasurementDAOResponse.create(x, measurementDetailsRepository.findByMeasurementId(x.getId())))
                .collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No measurements related with the user " + userId + " and with the tile data found");
    }

    private List<MeasurementDAOResponse> getMeasurementFromTileMeasurement(InformationTileMeasurement tileM){
    	List<MeasurementDAOResponse> list = Collections.singletonList(MeasurementDAOResponse.create(tileM.getMeasurement(), measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId())));
    	
    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No measurements related with the tile data found");
    }
    
	public InformationPanelDAOResponse connect(Long id, Long assetId) {
		boolean panelExists = informationPanelRepository.existsById(id);

		if ( panelExists && assetRepository.existsById(assetId)) {
			InformationPanel asset = informationPanelRepository.findById(id).get();
			asset.getAssets().add(assetRepository.findById(assetId).get());
			return InformationPanelDAOResponse.create(informationPanelRepository.save(asset));
		} else if (!panelExists) throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
		else throw new InvalidNonExistingIdException("The asset to connect doesn't exists");
	}
}
