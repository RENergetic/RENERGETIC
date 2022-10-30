package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.InformationPanelDAORequest;
import com.renergetic.hdrapi.dao.InformationPanelDAOResponse;
import com.renergetic.hdrapi.dao.InformationTileDAOResponse;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.mapper.InformationPanelMapper;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTileMeasurement;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.repository.AssetRepository;
import com.renergetic.hdrapi.repository.InformationPanelRepository;
import com.renergetic.hdrapi.repository.MeasurementRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InformationPanelService {
    @Autowired
    private InformationPanelRepository informationPanelRepository;

    @Autowired
    private InformationPanelMapper informationPanelMapper;
    @Autowired
    private UuidRepository uuidRepository;

    @Autowired
    private MeasurementDetailsRepository measurementDetailsRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    public List<InformationPanelDAOResponse> getAll(long offset, int limit) {
        List<InformationPanelDAOResponse> list = informationPanelRepository.findAll(new OffSetPaging(offset, limit))
                .stream().map(x -> informationPanelMapper.toDTO(x)).collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No information panels exists");
    }

    public List<InformationPanelDAOResponse> getAll(Long ownerId) {
        List<InformationPanelDAOResponse> list = informationPanelRepository.findAllByOwnerId(ownerId).stream()
                .map(x -> informationPanelMapper.toDTO(x))
                .collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No information panels related with user " + ownerId + " found");
    }

    public InformationPanelDAOResponse getById(Long id) {
        return informationPanelMapper.toDTO(
                informationPanelRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public InformationPanelDAOResponse getByName(String name) {
        return informationPanelMapper.toDTO(
                informationPanelRepository.findByName(name).orElseThrow(NotFoundException::new));
    }

    public InformationPanelDAOResponse save(InformationPanelDAORequest informationPanel) {
        if (informationPanel.getId() != null && informationPanelRepository.existsById(informationPanel.getId()))
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Already exists a information panel with ID " + informationPanel.getId());

        InformationPanel infoPanelEntity = informationPanelMapper.toEntity(informationPanel);
        infoPanelEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        return informationPanelMapper.toDTO(informationPanelRepository.save(infoPanelEntity));
    }

    public InformationPanelDAOResponse update(InformationPanelDAORequest informationPanel) {
        if (informationPanel.getId() == null || !informationPanelRepository.existsById(informationPanel.getId()))
            throw new InvalidNonExistingIdException();

        return informationPanelMapper.toDTO(
                informationPanelRepository.save(informationPanelMapper.toEntity(informationPanel)));
    }

    public boolean deleteById(Long id) {
        if (id == null || !informationPanelRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        informationPanelRepository.deleteById(id);
        return true;
    }

    public InformationPanelDAOResponse connect(Long id, Long assetId) {
        boolean panelExists = id != null && informationPanelRepository.existsById(id);

        if (panelExists && assetRepository.existsById(assetId)) {
            InformationPanel asset = informationPanelRepository.findById(id).get();
            asset.getAssets().add(assetRepository.findById(assetId).get());
            return InformationPanelDAOResponse.create(informationPanelRepository.save(asset));
        } else if (!panelExists) throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
        else throw new InvalidNonExistingIdException("The asset to connect doesn't exists");
    }

    /**
     * find panels available definition by panelId
     *
     * @param userId panel Id
     * @param offset rows offset
     * @param limit  result length limit
     */
    public List<InformationPanelDAOResponse> findByUserId(Long userId, long offset, int limit) {
        /*
            Highly inefficient !!
            Just as a first draft to get it working but there is really a structural issue in the data highlighted here as this is a pure mess to get the request.
        */
        List<InformationPanel> informationPanels = informationPanelRepository.findByUserId(userId, offset, limit);
        List<InformationPanelDAOResponse> list =
                informationPanels.stream().map(panel ->
                        InformationPanelDAOResponse.create(panel,
                                panel.getTiles().stream().map(tile ->
                                        InformationTileDAOResponse.create(tile,
                                                tile.getInformationTileMeasurements()
                                                        .stream().map(this::getMeasurementFromTileMeasurement)
                                                        .collect(Collectors.toList())
                                        )).collect(Collectors.toList())
                        )).collect(Collectors.toList());

        if (list.size() > 0)
            return list;
        else throw new NotFoundException("No information panels related with userId " + userId + " found");
    }

    /**
     * get panel template by panelId and infer measurements by assetId
     *
     * @param panelId
     * @param assetId
     * @return
     */
    public InformationPanelDAOResponse getAssetTemplate(Long panelId, Long assetId) {
        /*
            Highly inefficient !!
            Just as a first draft to get it working but there is really a structural issue in the data highlighted here as this is a pure mess to get the request.
        */

        Optional<InformationPanel> panel = informationPanelRepository.findById(panelId);
        if (panel.isPresent()) {
            InformationPanelDAOResponse p = InformationPanelDAOResponse.create(panel.get(),
                    panel.get().getTiles().stream().map(tile -> InformationTileDAOResponse.create(tile,
                            tile.getInformationTileMeasurements().stream().map(
                                    tileM -> tileM.getMeasurement() == null
                                            && panel.get().getIsTemplate() && assetId != null
                                            && tileM.getAssetCategory() == null ?
                                            getInferredMeasurements(panelId, tileM, assetId)
                                            : Collections.singletonList(getMeasurementFromTileMeasurement(tileM)))
                                    .flatMap(List::stream).collect(Collectors.toList())
                    )).collect(Collectors.toList())
            );
            return p;
        } else throw new NotFoundException("No information panel  " + panelId + " not found");
    }

    public List<Measurement> getPanelMeasurements(long id) {
        InformationPanel panel = informationPanelRepository.findById(id).orElse(null);

        if (panel != null) {
            return panel.getTiles().stream().map(tile -> tile.getInformationTileMeasurements()
                    .stream().map(InformationTileMeasurement::getMeasurement)
                    .collect(Collectors.toList())
            ).flatMap(List::stream).collect(Collectors.toList());
        }
        throw new NotFoundException("No panel found related with id " + id);
    }



    private List<MeasurementDAOResponse> getInferredMeasurements(Long userId, InformationTileMeasurement tileM,
                                                                    Long assetId) {

        List<MeasurementDAOResponse> list = measurementRepository
                .findByAssetIdAndBySensorNameAndDomainAndDirectionAndType(assetId,
                        tileM.getMeasurementName(),
                        tileM.getSensorName(),
                        tileM.getDomain().name(), tileM.getDirection().name(), tileM.getType().getId())
                .stream().map(x -> MeasurementDAOResponse.create(x,
                        measurementDetailsRepository.findByMeasurementId(x.getId())))
                .collect(Collectors.toList());

        if (list.size() > 0)
            return list;
        else throw new NotFoundException(
                "No measurements related with the user " + userId + " and  asset " + assetId + "with the tile data found");
    }


    private MeasurementDAOResponse getMeasurementFromTileMeasurement(InformationTileMeasurement tileM) {
        if (tileM.getMeasurement() == null) {
            return null;
        }
        var details = measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
        return MeasurementDAOResponse.create(tileM.getMeasurement(), details);
    }


    //    public List<Measurement> getAssetTemplateMeasurements(Long panelId, Long assetId) {
//
//        Optional<InformationPanel> panel = informationPanelRepository.findById(panelId);
//        if (panel.isPresent()) {
//            List<Measurement> list = panel.get().getTiles().stream().map(tile ->
//                    tile.getInformationTileMeasurements().stream().map(
//                            tileM -> tileM.getMeasurement() == null
//                                    && panel.get().getIsTemplate() && assetId != null
//                                    && tileM.getAssetCategory() == null ?
//                                    getInferredMeasurements(tileM, assetId)
//                                    : Collections.singletonList(tileM.getMeasurement()))
//                            .flatMap(List::stream).collect(Collectors.toList())
//            ).flatMap(List::stream).collect(Collectors.toList())
//                    ;
//            if (list.size() > 0)
//                return list;
//            else throw new NotFoundException(
//                    "No measurements related with the   asset " + assetId + "with the tile data found");
//        } else throw new NotFoundException("No information panels related with panelId " + panelId + " found");
//
//    }

//    private MeasurementDAOResponse getMeasurementFromTileMeasurement(InformationTileMeasurement tileM) {
//        if (tileM.getMeasurement() == null) {
//            return null;
//        }
//        var details = measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
//        return MeasurementDAOResponse.create(tileM.getMeasurement(), details);
//    }

//    private List<MeasurementDAOResponse> getMeasurementsFromTileMeasurement(InformationTileMeasurement tileM) {
//        if (tileM.getMeasurement() == null) {
//            return Collections.emptyList();
//        }
//        var details = measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
//        List<MeasurementDAOResponse> list = Collections.singletonList(
//                MeasurementDAOResponse.create(tileM.getMeasurement(), details));
//
////        if (list != null && list.size() > 0)
//        return list;
////        else throw new NotFoundException("No measurements related with the tile data found");
//    }


}
