package com.renergetic.baseapi.service;

import com.renergetic.common.dao.*;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.mapper.InformationPanelMapper;
import com.renergetic.common.model.*;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.common.mapper.InformationTilePanelMapper;
import com.renergetic.common.utilities.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InformationPanelService {
    @Autowired
    private MeasurementService measurementService;

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

    @Autowired
    private InformationTilePanelMapper informationTilePanelMapper;

    public List<InformationPanelDAOResponse> getAll(long offset, int limit) {
        return informationPanelRepository.findAll(new OffSetPaging(offset, limit,
                        Sort.by(Sort.Direction.ASC, "id")))
                .stream().map(x -> informationTilePanelMapper.toDTO(x, false)).collect(Collectors.toList());


    }

    public List<InformationPanelDAOResponse> getAll(Long ownerId) {
        List<InformationPanelDAOResponse> list = informationPanelRepository.findAllByOwnerId(ownerId).stream()
                .map(x -> informationTilePanelMapper.toDTO(x, false))
                .collect(Collectors.toList());

        if (list != null && !list.isEmpty())
            return list;
        else throw new NotFoundException("No information panels related with user " + ownerId + " found");
    }

    public InformationPanelDAOResponse getById(Long id, Boolean detailed) {
        return informationTilePanelMapper.toDTO(
                informationPanelRepository.findById(id).orElseThrow(NotFoundException::new), detailed);
    }

    public InformationPanelDAOResponse getByName(String name) {
        return informationTilePanelMapper.toDTO(
                informationPanelRepository.findByName(name).orElseThrow(NotFoundException::new));
    }

    public InformationPanelDAOResponse save(InformationPanelDAORequest informationPanel) {
        if (informationPanel.getId() != null && informationPanelRepository.existsById(informationPanel.getId()))
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Already exists a information panel with ID " + informationPanel.getId());

        InformationPanel infoPanelEntity = informationPanelMapper.toEntity(informationPanel);
        infoPanelEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        return informationTilePanelMapper.toDTO(informationPanelRepository.save(infoPanelEntity));
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
        Long id = informationPanel.getId();
        InformationPanel panel = informationPanelRepository.findById(id).orElse(null);

        if (panel != null)
            deleteTiles(panel);

        informationPanelRepository.flush();
        InformationPanel infoPanelEntity = informationPanel.mapToEntity();
        infoPanelEntity.setId(id);
        this.saveTiles(infoPanelEntity.getTiles(), infoPanelEntity);

        if (panel != null)
            infoPanelEntity.setAssets(panel.getAssets());

        infoPanelEntity = informationPanelRepository.save(infoPanelEntity);
        return informationTilePanelMapper.toDTO(infoPanelEntity, false);


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
        return informationTilePanelMapper.toDTO(infoPanelEntity, false);
    }

    public InformationPanelDAO inferMeasurements(InformationPanelDAO informationPanel) {
        var tiles = informationPanel.getTiles();
        MeasurementTags panelTag;
        if (informationPanel.getProps().containsKey("tag_key")) {
            panelTag = new MeasurementTags();
            panelTag.setKey(informationPanel.getProps().get("tag_key").toString());
            if (informationPanel.getProps().containsKey("tag_value"))
                panelTag.setValue(informationPanel.getProps().get("tag_value").toString());
        } else {
            panelTag = new MeasurementTags();
        }
        tiles.forEach(tile -> {
            MeasurementTags mtag;
            if (tile.getProps().containsKey("tag_key")) {
                mtag = new MeasurementTags();
                mtag.setKey(tile.getProps().get("tag_key").toString());
                if (tile.getProps().containsKey("tag_value"))
                    mtag.setValue(tile.getProps().get("tag_value").toString());
            } else
                mtag = new MeasurementTags();

            List<MeasurementTileDAORequest> collect = tile.getMeasurements()
                    .stream().map(
                            tileM -> {
                                if (tileM.getId() == null) {
                                    //infer type
                                    if (tileM.getType() != null
                                            && (tileM.getType().getId() != null
                                            || tileM.getType().getPhysicalName() == null)) {
                                        //TODO: map to entity tileM.mapToEntity
                                        tileM.setType(measurementService.verifyMeasurementType(tileM.getType()));
                                    }
                                    Long assetId = null;
                                    if (tileM.getAsset() != null) {
                                        Optional<Asset> byName = assetRepository.findByName(tileM.getAsset().getName());
                                        if (byName.isPresent()) {
                                            assetId = byName.get().getId();
                                        }
                                    }


                                    var l = getInferredMeasurements(tileM, assetId, panelTag, mtag);
                                    if (l.isEmpty()) {
                                        //return non inferred measurements so the client can see what's missing
                                        return List.of(tileM);
                                    }
                                    return l;
                                } else {
                                    // FIXME: here a measurement is retrieved from the database but it isn't used
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
        infoPanelEntity.setTiles(null);
        tiles.stream().forEach(it -> {
            it.setId(null);
            it.setInformationPanel(infoPanelEntity);
            informationTileRepository.save(it);
            it.getInformationTileMeasurements().stream().forEach(tm ->
            {
                if (tm.getType() != null)
                    tm.setType(measurementService.verifyMeasurementType(tm.getType()));
                tm.setId(null);
                tm.setInformationTile(it);
                informationTileMeasurementRepository.save(tm);
            });
        });
    }

    public boolean deleteById(Long id) {
        var panel = informationPanelRepository.findById(id).orElseThrow(InvalidNonExistingIdException::new);
        deleteTiles(panel);
        informationPanelRepository.delete(panel);
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
        boolean panelExists = id != null && informationPanelRepository.existsById(id);

        if (panelExists && assetRepository.existsById(assetId)) {
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
        InformationPanel panel = informationPanelRepository.findById(id)
                .orElseThrow(() -> new InvalidNonExistingIdException("The panel to connect doesn't exists"));
        panel.setFeatured(state);
        panel = informationPanelRepository.save(panel);
        return panel.getFeatured();
    }


//    public InformationPanelDAOResponse connect(Long id, Long assetId) {
//        InformationPanel panel = informationPanelRepository.findById(id)
//                .orElseThrow(() -> new InvalidNonExistingIdException("The panel to connect doesn't exists"));
//
//        panel.getAssets().add(
//                assetRepository.findById(assetId)
//                        .orElseThrow(() -> new InvalidNonExistingIdException("The asset to connect doesn't exists")));
//        return InformationPanelDAOResponse.create(informationPanelRepository.save(panel));
//    }

//    /**
//     * find panels available definition by panelId
//     *
//     * @param userId panel Id
//     * @param offset rows offset
//     * @param limit  result length limit
//     */
//    public List<InformationPanelDAOResponse> findByUserId(Long userId, long offset, int limit) {
//        // TODO: Improve request efficiency
//        List<InformationPanel> informationPanels = informationPanelRepository.findByUserId(userId, offset, limit);
//        List<InformationPanelDAOResponse> list =
//                informationPanels.stream()
//                        .map(panel -> InformationPanelDAOResponse.create(panel, null))
//                        .collect(Collectors.toList());
//
//        if (!list.isEmpty())
//            return list;
//        else throw new NotFoundException("No information panels related with userId " + userId + " found");
//    }
//
//    /**
//     * @param isTemplate
//     * @param limit      result length limit
//     */
//    public List<InformationPanelDAOResponse> findFeatured(boolean isTemplate, int limit) {
//        // TODO: Improve request efficiency
//        List<InformationPanel> informationPanels = informationPanelRepository.findFeatured(isTemplate, 0, limit);
//        return informationPanels.stream()
//                .map(panel -> InformationPanelDAOResponse.create(panel, null))
//                .collect(Collectors.toList());
//    }

//    /**
//     * get panel template by panelId and infer measurements by assetId
//     *
//     * @param panelId
//     * @param assetId
//     * @return
//     */
//    public InformationPanelDAOResponse getAssetTemplate(Long panelId, Long assetId) {
//
//        // TODO: Improve request efficiency
//        Optional<InformationPanel> panel = informationPanelRepository.findById(panelId);
//        if (panel.isPresent()) {
//            return InformationPanelDAOResponse.create(panel.get(),
//                    panel.get().getTiles().stream().map(tile -> InformationTileDAOResponse.create(tile,
//                            tile.getInformationTileMeasurements().stream().map(
//                                            tileM -> tileM.getMeasurement() == null
//                                                    && panel.get().getIsTemplate() && assetId != null
//                                                    && tileM.getAssetCategory() == null ?
//                                                    getInferredMeasurements(tileM, assetId, tileM.getFunction())
//                                                    : Collections.singletonList(getMeasurementFromTileMeasurement(tileM)))
//                                    .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList())
//                    )).collect(Collectors.toList())
//            );
//        } else throw new NotFoundException("No information panel  " + panelId + " not found");
//    }


//    public List<InformationTileMeasurement> getPanelMeasurements(long id) {
//        InformationPanel panel = informationPanelRepository.findById(id).orElse(null);
//
//        if (panel != null) {
//            return panel.getTiles().stream().map(tile -> tile.getInformationTileMeasurements()
//                    .stream().filter(Objects::nonNull)
//                    .collect(Collectors.toList())
//            ).flatMap(List::stream).collect(Collectors.toList());
//        }
//        throw new NotFoundException("No panel found related with id " + id);
//    }

//    public List<Measurement> getTileMeasurements(long tileId, Long assetId, Long userId) {
//
//        InformationTile tile = informationTileRepository.findById(tileId).orElse(null);
//        if (tile != null) {
//            if (assetId != null) {
//                Boolean isTemplate = tile.getInformationPanel().getIsTemplate();
//                //TODO: simplify it
//                return tile.getInformationTileMeasurements().stream().map(
//                                tileM -> tileM.getMeasurement() == null
//                                        && isTemplate
//                                        && tileM.getAssetCategory() == null ?
//                                        getInferredMeasurements(tileM, assetId, tileM.getFunction()).stream().map(
//                                                MeasurementDAOResponse::mapToEntity).collect(Collectors.toList())
//                                        : Collections.singletonList(tileM.getMeasurement()))
//                        .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
//            } else {
//                return tile.getInformationTileMeasurements().stream().map(
//                        InformationTileMeasurement::getMeasurement).filter(Objects::nonNull).collect(
//                        Collectors.toList());
//            }
//        }
//        throw new NotFoundException("No tile found related with tile id: " + tileId);
//    }

    private List<MeasurementTileDAORequest> getInferredMeasurements(MeasurementTileDAORequest tileM, Long assetId,
                                                                    MeasurementTags panelTag, MeasurementTags mTag) {
        return measurementRepository
                .inferMeasurement(assetId,
                        tileM.getName(),
                        tileM.getSensorName(),
                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
                        tileM.getType() != null ? tileM.getType().getId() : null,
                        tileM.getType() != null ? tileM.getType().getPhysicalName() : null
                        , mTag.getKey(), mTag.getValue(), panelTag.getKey(), panelTag.getValue())
                .stream().map((m) -> MeasurementTileDAORequest.create(m, tileM.getFunction(), tileM.getProps()))
                .collect(Collectors.toList());
    }

//    private List<MeasurementDAOResponse> getInferredMeasurements(InformationTileMeasurement tileM,
//                                                                 Long assetId, String func) {
//        return measurementRepository
//                .inferMeasurement(assetId,
//                        tileM.getMeasurementName(),
//                        tileM.getSensorName(),
//                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
//                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
//                        tileM.getType() != null ? tileM.getType().getId() : null,
//                        tileM.getPhysicalName())
//                .stream().map(x -> MeasurementDAOResponse.create(x,
//                        measurementDetailsRepository.findByMeasurementId(x.getId()), func))
//                .collect(Collectors.toList());
//    }

//    private MeasurementDAOResponse getMeasurementFromTileMeasurement(InformationTileMeasurement tileM) {
//        if (tileM.getMeasurement() == null) {
//            return null;
//        }
//        List<MeasurementDetails> details =
//                measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
//        return MeasurementDAOResponse.create(tileM.getMeasurement(), details, tileM.getFunction());
//    }
}
