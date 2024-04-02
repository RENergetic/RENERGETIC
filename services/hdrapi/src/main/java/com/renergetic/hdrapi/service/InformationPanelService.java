package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.*;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.mapper.InformationPanelMapper;
import com.renergetic.common.model.*;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.dao.tempcommon.InformationPanelMapperTemp;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformationPanelService {
    @Autowired
    private MeasurementService measurementService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private InformationPanelRepository informationPanelRepository;
    @Autowired
    private InformationTileRepository informationTileRepository;
    @Autowired
    private InformationTileMeasurementRepository informationTileMeasurementRepository;

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
        return informationPanelRepository.findAll(new OffSetPaging(offset, limit,
                        Sort.by(Sort.Direction.ASC, "id")))
                .stream().map(x -> informationPanelMapper.toDTO(x, false)).collect(Collectors.toList());


    }

    public List<InformationPanelDAOResponse> getAll(Long ownerId) {
        List<InformationPanelDAOResponse> list = informationPanelRepository.findAllByOwnerId(ownerId).stream()
//                .map(x -> informationPanelMapper.toDTO(x))
                .map(x -> informationPanelMapper.toDTO(x, false))
                .collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No information panels related with user " + ownerId + " found");
    }

    //    public InformationPanelDAOResponse getById(Long id ) {
//        return this.getById(id,false)
//    }
    public InformationPanelDAOResponse getById(Long id, Boolean detailed) {
//        return informationPanelMapper.toDTO(
        return informationPanelMapper.toDTO(
                informationPanelRepository.findById(id).orElseThrow(NotFoundException::new), detailed);
    }

    public InformationPanelDAOResponse getByName(String name) {
//        return informationPanelMapper.toDTO(
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
//        return informationPanelMapper.toDTO(informationPanelRepository.save(infoPanelEntity));
    }

    public InformationPanelDAOResponse update(InformationPanelDAO informationPanel) {
        if (informationPanel.getId() != null &&
                !informationPanelRepository.findById(informationPanel.getId()).isPresent())
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Panel with given id does not exists: " + informationPanel.getId());
        else if (informationPanel.getId() == null
                && !informationPanelRepository.findByName(informationPanel.getName()).isPresent())
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Panel with given name does not exists: " + informationPanel.getName());
        var id = informationPanel.getId();
        var panel = informationPanelRepository.getById(id);
        deleteTiles(panel);
        informationPanelRepository.flush();
        InformationPanel infoPanelEntity = informationPanel.mapToEntity();
        infoPanelEntity.setId(id);
        this.saveTiles(infoPanelEntity.getTiles(), infoPanelEntity);
        infoPanelEntity.setAssets(panel.getAssets());
        infoPanelEntity = informationPanelRepository.save(infoPanelEntity);
//        return informationPanelMapper.toDTO(infoPanelEntity); //uncomment with new common version
        return informationPanelMapper.toDTO(infoPanelEntity, false);


    }

    @Transactional
    public InformationPanelDAOResponse save(InformationPanelDAO informationPanel) {
        if (informationPanelRepository.findByName(informationPanel.getName()).isPresent())
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Already exists a information panel with name " + informationPanel.getName());

        InformationPanel infoPanelEntity = informationPanel.mapToEntity();
        var tiles = infoPanelEntity.getTiles();
        var uuid = uuidRepository.save(new UUID());
        infoPanelEntity.setUuid(uuid);
        infoPanelEntity.setTiles(null);
        infoPanelEntity = informationPanelRepository.save(infoPanelEntity);
        this.saveTiles(tiles, infoPanelEntity);
        informationPanelRepository.flush();
//        return informationPanelMapper.toDTO(infoPanelEntity);
        return informationPanelMapper.toDTO(infoPanelEntity, false);
    }

    public InformationPanelDAO inferMeasurements(InformationPanelDAO informationPanel) {
        var tiles = informationPanel.getTiles();

        tiles.forEach(tile -> {
            List<MeasurementTileDAORequest> collect = tile.getMeasurements()
                    .stream().map(
                            tileM -> {
                                if (tileM.getId() == null) {
                                    //infer type
                                    if (tileM.getType() != null
                                            && (tileM.getType().getId() != null
                                            || tileM.getType().getPhysicalName() == null)) {
                                        //TODO map to entity tileM.mapToEntity
                                        tileM.setType(measurementService.verifyMeasurementType(tileM.getType()));
                                    }
                                    if (tileM.getAsset() != null && tileM.getAsset().getId() != null) {
                                        Optional<Asset> byId = assetRepository.findById(tileM.getId());
                                        if (byId.isPresent()) {
                                            tileM.setAsset(SimpleAssetDAO.create(byId.get()));
                                        } else {
                                            tileM.getAsset().setId(null);
                                        }
                                    }
                                    var l = getInferredMeasurements(tileM);
                                    if (l.isEmpty()) {
                                        //return non inferred measurements so the client can see what's missing
                                        return List.of(tileM);
                                    }
                                    return l;
                                } else {
                                    measurementRepository.findById(tileM.getId()).orElseThrow(
                                            () -> new NotFoundException("Measurement not found " + tileM.getId()));
                                    return List.of(tileM);
                                }

                            }
                    )
                    .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
            if (!collect.isEmpty())
                tile.setMeasurements(collect);
        });
        return informationPanel;
    }


    private void saveTiles(List<InformationTile> tiles, InformationPanel infoPanelEntity) {
//        var tiles = infoPanelEntity.getTiles();
        infoPanelEntity.setTiles(null);
        tiles.stream().forEach(it -> {
            it.setId(null);
            it.setInformationPanel(infoPanelEntity);
            informationTileRepository.save(it);
            it.getInformationTileMeasurements().stream().forEach(tm ->
            {
                if (tm.getMeasurement() != null) {
                    var m = new InformationTileMeasurement();
                    tm.setId(null);
                    m.setInformationTile(it);
                    m.setFunction(tm.getFunction());
                    m.setMeasurement(tm.getMeasurement());
                    m.setProps(tm.getProps());
                    informationTileMeasurementRepository.save(m);
                } else {
                    if (tm.getType() != null)
                        tm.setType(measurementService.verifyMeasurementType(tm.getType()));
                    if (tm.getAsset() != null) {
                        Asset mAsset = null;
                        if (tm.getAsset().getId() != null) {
                            mAsset = assetRepository.findById(tm.getAsset().getId()).orElse(null);
                        }
                        if (mAsset == null && tm.getAsset().getName() != null) {
                            mAsset = assetRepository.findByName(tm.getAsset().getName()).orElse(null);
                        }
                        tm.setAsset(mAsset);

                    }
                    tm.setId(null);
                    tm.setInformationTile(it);
                    informationTileMeasurementRepository.save(tm);
                }


            });
        });
    }

//    public InformationPanelDAOResponse update(InformationPanelDAORequest informationPanel) {
//        if (informationPanel.getId() == null || !informationPanelRepository.existsById(informationPanel.getId()))
//            throw new InvalidNonExistingIdException();
////TODO: validate, check user privileges
//        var panel = informationPanelRepository.getById(informationPanel.getId());
//        return informationPanelMapper.toDTO(
//                informationPanelRepository.save(informationPanelMapper.toEntity(informationPanel, panel)));
//    }

    public boolean deleteById(Long id) {
        if (id == null || !informationPanelRepository.existsById(id))
            throw new InvalidNonExistingIdException();
        var panel = informationPanelRepository.getById(id);
        deleteTiles(panel);
        informationPanelRepository.delete(panel);
//        informationPanelRepository.deleteById(id);
        return true;
    }

    private void deleteTiles(InformationPanel panel) {
        panel.getTiles().forEach(t -> {
                    t.getInformationTileMeasurements().forEach(m -> informationTileMeasurementRepository.delete(m));
                    informationTileRepository.delete(t);
                }
        );
    }

    public Boolean assign(Long id, Long assetId) {
        return this.setConnection(id, assetId, true);
    }

    public List<SimpleAssetDAO> getConnectedAssets(Long id) {
        boolean panelExists = id != null && informationPanelRepository.existsById(id);

        if (panelExists) {
            return informationPanelRepository.findConnectedAssets(id).stream()
                    .map(SimpleAssetDAO::create).collect(Collectors.toList());
        } else throw new InvalidNonExistingIdException("The panel to connect doesn't exists");

    }


    public Boolean revoke(Long id, Long assetId) {
        return this.setConnection(id, assetId, false);
    }

    public Boolean setConnection(Long id, Long assetId, boolean state) {
        //TODO: check user priveleges
        boolean panelExists = id != null && informationPanelRepository.existsById(id);

        if (panelExists && assetRepository.existsById(assetId)) {
//            InformationPanel asset = informationPanelRepository.findById(id).get();
            if (state)
                informationPanelRepository.assignAsset(id, assetId);
            else {
                informationPanelRepository.revokeAsset(id, assetId);

            }
            return true;
        } else if (!panelExists) throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
        else throw new InvalidNonExistingIdException("The asset to connect doesn't exists");
    }


    public Boolean setFeatured(Long id, boolean state) {
        boolean panelExists = id != null && informationPanelRepository.existsById(id);

        if (panelExists) {
            InformationPanel panel = informationPanelRepository.findById(id).get();
            panel.setFeatured(state);
            panel = informationPanelRepository.save(panel);
            return panel.getFeatured();
        } else throw new InvalidNonExistingIdException("The panel to connect doesn't exists");

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
                informationPanels.stream()
                        .map(panel -> InformationPanelDAOResponse.create(panel, null))
                        .collect(Collectors.toList());

        if (list.size() > 0)
            return list;
        else throw new NotFoundException("No information panels related with userId " + userId + " found");
    }

    /**
     * @param isTemplate
     * @param limit      result length limit
     */
    public List<InformationPanelDAOResponse> findFeatured(boolean isTemplate, int limit) {
        /*
            Highly inefficient !!
            Just as a first draft to get it working but there is really a structural issue in the data highlighted here as this is a pure mess to get the request.
        */
        List<InformationPanel> informationPanels = informationPanelRepository.findFeatured(isTemplate, 0, limit);
        List<InformationPanelDAOResponse> list =
                informationPanels.stream()
                        .map(panel -> {
                            var p = InformationPanelDAOResponse.create(panel, null);
                            return p;
                        })
                        .collect(Collectors.toList());


        return list;
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
            return InformationPanelDAOResponse.create(panel.get(),
                    panel.get().getTiles().stream().map(tile -> InformationTileDAOResponse.create(tile,
                            tile.getInformationTileMeasurements().stream().map(
                                            tileM -> tileM.getMeasurement() == null
                                                    && panel.get().getIsTemplate() && assetId != null
                                                    && tileM.getAssetCategory() == null ?
                                                    getInferredMeasurements(tileM, assetId, tileM.getFunction())
                                                    : Collections.singletonList(getMeasurementFromTileMeasurement(tileM)))
                                    .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList())
                    )).collect(Collectors.toList())
            );
        } else throw new NotFoundException("No information panel  " + panelId + " not found");
    }


    public List<InformationTileMeasurement> getPanelMeasurements(long id) {
        InformationPanel panel = informationPanelRepository.findById(id).orElse(null);

        if (panel != null) {
            return panel.getTiles().stream().map(tile -> tile.getInformationTileMeasurements()
                    .stream().filter(Objects::nonNull)
                    .collect(Collectors.toList())
            ).flatMap(List::stream).collect(Collectors.toList());
        }
        throw new NotFoundException("No panel found related with id " + id);
    }

    public List<Measurement> getTileMeasurements(long tileId, Long assetId, Long userId) {

        InformationTile tile = informationTileRepository.findById(tileId).orElse(null);
        //TODO: check if user has access to the panel
        //var panel = tile.getInformationPanel()
        if (tile != null) {
            if (assetId != null) {
                Boolean isTemplate = tile.getInformationPanel().getIsTemplate();
                //TODO: simplify it
                return tile.getInformationTileMeasurements().stream().map(
                                tileM -> tileM.getMeasurement() == null
                                        && isTemplate
                                        && tileM.getAssetCategory() == null ?
                                        getInferredMeasurements(tileM, assetId, tileM.getFunction()).stream().map(
                                                MeasurementDAOResponse::mapToEntity).collect(Collectors.toList())
                                        : Collections.singletonList(tileM.getMeasurement()))
                        .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
            } else {
                return tile.getInformationTileMeasurements().stream().map(
                        InformationTileMeasurement::getMeasurement).filter(Objects::nonNull).collect(
                        Collectors.toList());
            }
        }
        throw new NotFoundException("No tile found related with tile id: " + tileId);
    }

    private List<MeasurementTileDAORequest> getInferredMeasurements(MeasurementTileDAORequest tileM) {
        List<MeasurementTileDAORequest> list = measurementRepository
                .inferMeasurement(null,
                        tileM.getName(),
                        tileM.getSensorName(),
                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
                        tileM.getType() != null ? tileM.getType().getId() : null,
                        tileM.getType() != null ? tileM.getType().getPhysicalName() : null)
                .stream().map((m) -> MeasurementTileDAORequest.create(m, tileM.getFunction(), tileM.getProps()))
                .collect(Collectors.toList());
        return list;
    }

    private List<MeasurementDAOResponse> getInferredMeasurements(InformationTileMeasurement tileM,
                                                                 Long assetId, String func) {


        List<MeasurementDAOResponse> list = measurementRepository
                .inferMeasurement(assetId,
                        tileM.getMeasurementName(),
                        tileM.getSensorName(),
                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
                        tileM.getType() != null ? tileM.getType().getId() : null,
                        tileM.getPhysicalName())
                .stream().map(x -> MeasurementDAOResponse.create(x,
                        measurementDetailsRepository.findByMeasurementId(x.getId()), func))
                .collect(Collectors.toList());
//TODO: throw some exception code to the UI - so the users know that not all measurements are not available for this asset
//        if (list.size() > 0)
        return list;
//        else throw new NotFoundException(
//                "No measurements related with the user:" + (userId != null ? userId.toString() : " N/A ") + " and  asset " + assetId + "with the tile data found");
    }


    private MeasurementDAOResponse getMeasurementFromTileMeasurement(InformationTileMeasurement tileM) {
        if (tileM.getMeasurement() == null) {
            return null;
        }
        List<MeasurementDetails> details =
                measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
        return MeasurementDAOResponse.create(tileM.getMeasurement(), details, tileM.getFunction());
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
