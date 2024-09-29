package com.renergetic.dataapi.service;

import java.util.*;
import java.util.stream.Collectors;

import com.renergetic.common.dao.*;
import com.renergetic.common.model.*;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.*;
import com.renergetic.common.utilities.Json;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.mapper.InformationPanelMapper;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.common.utilities.OffSetPaging;

@Service
public class InformationPanelService {
    @Autowired
    MeasurementTypeRepository measurementTypeRepository;

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

//    public List<InformationPanelDAOResponse> getAll(long offset, int limit) {
//        List<InformationPanelDAOResponse> list = informationPanelRepository.findAll(new OffSetPaging(offset, limit))
//                .stream().map(x -> informationPanelMapper.toDTO(x)).collect(Collectors.toList());
//
//        if (list != null && list.isEmpty())
//            return list;
//        else throw new NotFoundException("No information panels exists");
//    }
//
//    public List<InformationPanelDAOResponse> getAll(Long ownerId) {
//        List<InformationPanelDAOResponse> list = informationPanelRepository.findAllByOwnerId(ownerId).stream()
//                .map(x -> informationPanelMapper.toDTO(x))
//                .collect(Collectors.toList());
//
//        if (list != null && list.isEmpty())
//            return list;
//        else throw new NotFoundException("No information panels related with user " + ownerId + " found");
//    }
//
//    public InformationPanelDAOResponse getById(Long id, Boolean detailed) {
//        return informationPanelMapper.toDTO(
//                informationPanelRepository.findById(id).orElseThrow(NotFoundException::new), detailed);
//    }
//
//    public InformationPanelDAOResponse getByName(String name) {
//        return informationPanelMapper.toDTO(
//                informationPanelRepository.findByName(name).orElseThrow(NotFoundException::new));
//    }
//
//    public InformationPanelDAOResponse save(InformationPanelDAORequest informationPanel) {
//        if (informationPanel.getId() != null && informationPanelRepository.existsById(informationPanel.getId()))
//            throw new InvalidCreationIdAlreadyDefinedException(
//                    "Already exists a information panel with ID " + informationPanel.getId());
//
//        InformationPanel infoPanelEntity = informationPanelMapper.toEntity(informationPanel);
//        infoPanelEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
//        return informationPanelMapper.toDTO(informationPanelRepository.save(infoPanelEntity));
//    }

//    public InformationPanelDAOResponse update(InformationPanelDAO informationPanel) {
//        Long id = informationPanel.getId();
//        InformationPanel panel = informationPanelRepository.findById(id).orElseThrow(InvalidNonExistingIdException::new);
//        deleteTiles(panel);
//        informationPanelRepository.flush();
//        InformationPanel infoPanelEntity = informationPanel.mapToEntity();
//        infoPanelEntity.setId(id);
//        this.saveTiles(infoPanelEntity);
//        infoPanelEntity.setAssets(panel.getAssets());
//        infoPanelEntity = informationPanelRepository.save(infoPanelEntity);
//        return informationPanelMapper.toDTO(infoPanelEntity);
//
//
//    }

//    public InformationPanelDAOResponse save(InformationPanelDAO informationPanel) {
//        if (informationPanelRepository.findByName(informationPanel.getName()).isPresent())
//            throw new InvalidCreationIdAlreadyDefinedException(
//                    "Already exists a information panel with name " + informationPanel.getName());
//
//        InformationPanel infoPanelEntity = informationPanel.mapToEntity();
//        infoPanelEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
//        informationPanelRepository.save(infoPanelEntity);
//        this.saveTiles(infoPanelEntity);
//        return informationPanelMapper.toDTO(infoPanelEntity);
//    }

//    private void saveTiles(InformationPanel infoPanelEntity) {
//        var tiles = infoPanelEntity.getTiles();
//        infoPanelEntity.setTiles(null);
//        tiles.stream().forEach(it -> {
//            it.setId(null);
//            it.setInformationPanel(infoPanelEntity);
//            informationTileRepository.save(it);
//            it.getInformationTileMeasurements().stream().forEach(tm ->
//            {
//                tm.setId(null);
//                tm.setInformationTile(it);
//                informationTileMeasurementRepository.save(tm);
//            });
//        });
//    }

//    public InformationPanelDAOResponse update(InformationPanelDAORequest informationPanel) {
//        InformationPanel panel = informationPanel.getId() != null ? informationPanelRepository.findById(informationPanel.getId()).orElse(null) : null;
//
//        if (panel == null)
//            throw new InvalidNonExistingIdException();
//
//        //TODO: validate, check user privileges
//        return informationPanelMapper.toDTO(
//                informationPanelRepository.save(panel));
//    }

//    public boolean deleteById(Long id) {
//        InformationPanel panel = informationPanelRepository.findById(id).orElseThrow(InvalidNonExistingIdException::new);
//        deleteTiles(panel);
//        informationPanelRepository.delete(panel);
//        return true;
//    }

//    private void deleteTiles(InformationPanel panel) {
//        panel.getTiles().forEach(t -> {
//                    t.getInformationTileMeasurements().forEach(m -> informationTileMeasurementRepository.delete(m));
//                    informationTileRepository.delete(t);
//                }
//        );
//    }
//
//    public Boolean assign(Long id, Long assetId) {
//        return this.setConnection(id, assetId, true);
//    }

//    public List<SimpleAssetDAO> getConnectedAssets(Long id) {
//        boolean panelExists = id != null && informationPanelRepository.existsById(id);
//
//        if (panelExists) {
//            return informationPanelRepository.findConnectedAssets(id).stream()
//                    .map(SimpleAssetDAO::create).collect(Collectors.toList());
//        } else throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
//
//    }
//
//
//    public Boolean revoke(Long id, Long assetId) {
//        return this.setConnection(id, assetId, false);
//    }

//    public Boolean setConnection(Long id, Long assetId, boolean state) {
//        //TODO: check user priveleges
//        boolean panelExists = id != null && informationPanelRepository.existsById(id);
//
//        if (panelExists && assetRepository.existsById(assetId)) {
//            if (state)
//                informationPanelRepository.assignAsset(id, assetId);
//            else {
//                informationPanelRepository.revokeAsset(id, assetId);
//
//            }
//            return true;
//        } else if (!panelExists) throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
//        else throw new InvalidNonExistingIdException("The asset to connect doesn't exists");
//    }

//
//    public Boolean setFeatured(Long id, boolean state) {
//        InformationPanel panel = id != null ? informationPanelRepository.findById(id).orElse(null) : null;
//
//        if (panel != null) {
//            panel.setFeatured(state);
//            panel = informationPanelRepository.save(panel);
//            return panel.getFeatured();
//        } else throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
//
//    }
//
//
//    public InformationPanelDAOResponse connect(Long id, Long assetId) {
//        InformationPanel panel = id != null ? informationPanelRepository.findById(id).orElse(null) : null;
//
//        if (panel == null)
//            throw new InvalidNonExistingIdException("The panel to connect doesn't exists");
//
//        panel.getAssets().add(assetRepository.findById(assetId).orElseThrow(() ->
//                new InvalidNonExistingIdException("The asset to connect doesn't exists")
//        ));
//        return InformationPanelDAOResponse.create(informationPanelRepository.save(panel));
//    }
//
//    /**
//     * find panels available definition by panelId
//     *
//     * @param userId panel Id
//     * @param offset rows offset
//     * @param limit  result length limit
//     */
//    public List<InformationPanelDAOResponse> findByUserId(Long userId, long offset, int limit) {
//        // TODO: inefficient, optimize query
//        List<InformationPanel> informationPanels = informationPanelRepository.findByUserId(userId, offset, limit);
//        List<InformationPanelDAOResponse> list =
//                informationPanels.stream()
//                        .map(panel -> InformationPanelDAOResponse.create(panel, null))
//                        .collect(Collectors.toList());
//
//        if (list.isEmpty())
//            return list;
//        else throw new NotFoundException("No information panels related with userId " + userId + " found");
//    }
//
//    /**
//     * @param isTemplate
//     * @param limit      result length limit
//     */
//    public List<InformationPanelDAOResponse> findFeatured(boolean isTemplate, int limit) {
//        // TODO: inefficient, optimize query
//        List<InformationPanel> informationPanels = informationPanelRepository.findFeatured(isTemplate, 0, limit);
//        return informationPanels.stream()
//                .map(panel -> InformationPanelDAOResponse.create(panel, null))
//                .collect(Collectors.toList());
//    }

    private InformationPanel requirePanel(Long panelId) {
        InformationPanel informationPanel = informationPanelRepository.findById(panelId).orElse(null);
        if (informationPanel == null) {
            throw new NotFoundException("No information panel  " + panelId + " not found");
        }
        return informationPanel;
    }

    private List<MeasurementDAOResponse> getInferredMeasurements(InformationTileMeasurement tileM, Long assetId,
                                                                 MeasurementTags panelTag, MeasurementTags mTag) {
        var l = measurementRepository
                .inferMeasurement(assetId,
                        tileM.getMeasurementName(),
                        tileM.getSensorName(),
                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
                        tileM.getType() != null ? tileM.getType().getId() : null,
                        tileM.getType() != null ? tileM.getType().getPhysicalName() : null
                        , mTag.getKey(), mTag.getValue(), panelTag.getKey(), panelTag.getValue())
                .stream().filter(Objects::nonNull)
                .map((m) -> MeasurementDAOResponse.create(m,
                        measurementDetailsRepository.findByMeasurementId(tileM.getId()), tileM.getFunction()))
                .collect(Collectors.toList());
        return l;

    }

    public List<Measurement> getTileMeasurements(long tileId, Long assetId, Long userId) {

        InformationTile tile = informationTileRepository.findById(tileId).orElse(null);
        //TODO: check if user has access to the panel
        if (tile != null) {
            Asset asset;
            if (assetId != null)
                asset = assetRepository.findById(assetId).orElseThrow(() ->
                        new InvalidNonExistingIdException("The asset  doesn't exists")
                );
            else asset = null;
            return this.inferTile(tile, new MeasurementTags(), asset).getMeasurements().stream()
                    .map(MeasurementDAOResponse::mapToEntity).collect(Collectors.toList());
        }
        throw new NotFoundException("No tile found related with tile id: " + tileId);
    }

    public InformationTileDAOResponse inferTile(InformationTile tile, MeasurementTags panelTag, Asset panelAsset) {
        MeasurementTags mTag = this.getTileTag(tile);
        List<MeasurementDAOResponse> collect = tile.getInformationTileMeasurements()
                .stream().map(
                        tileM -> {
                            if (tileM.getMeasurement() == null) {
                                //infer type
                                if (tileM.getType() != null
                                        && (tileM.getType().getId() != null
                                        || tileM.getType().getPhysicalName() == null)) {
                                    //TODO: map to entity tileM.mapToEntity
                                    tileM.setType(this.verifyMeasurementType(tileM.getType()));
                                }
                                Long assetId = null;

                                if (tileM.getAsset() != null) {
                                    Optional<Asset> byName = assetRepository.findByName(tileM.getAsset().getName());
                                    if (byName.isPresent()) {
                                        assetId = byName.get().getId();
                                    }
                                }
                                if (assetId == null && panelAsset != null) {
                                    assetId = panelAsset.getId();
                                }
                                var l = getInferredMeasurements(tileM, assetId, panelTag, mTag);
                                return l;
                            } else {
                                // FIXME: here a measurement is retrieved from the database but it isn't used
//                                var m = measurementRepository.findById(tileM.getId()).orElseThrow(
//                                        () -> new NotFoundException("Measurement not found " + tileM.getId()));
                                return List.of(MeasurementDAOResponse.create(tileM.getMeasurement(), measurementDetailsRepository.findByMeasurementId(tileM.getId()), tileM.getFunction()));

                            }

                        }
                )
                .flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
        return InformationTileDAOResponse.create(tile, collect);

    }

    public InformationPanelDAOResponse inferMeasurements(Long panelId, Long assetId) {
        InformationPanel p = this.requirePanel(panelId);
        var panelTag = this.getPanelTag(p);
        Asset asset;
        if (assetId != null)
            asset = assetRepository.findById(assetId).orElseThrow(() ->
                    new InvalidNonExistingIdException("The asset  doesn't exists")
            );
        else asset = null;
//    var tiles = informationPanel.getTiles();
        var informationPanel = InformationPanelDAOResponse.create(p,
                p.getTiles().stream().map(tile -> inferTile(tile, panelTag, asset)
                ).collect(Collectors.toList())
        );


        return informationPanel;
    }

//    /**
//     * get panel template by panelId and infer measurements by assetId
//     *
//     * @param panelId
//     * @param assetId
//     * @return
//     */
//    public InformationPanelDAOResponse getAssetTemplate(Long panelId, Long assetId) {
//        // TODO: inefficient, optimize query
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

    private MeasurementTags getPanelTag(InformationPanel panel) {
        MeasurementTags panelTag;
        Map<String, Object> props = null;
        try {
            props = Json.parse(panel.getProps()).toMap();
        } catch (ParseException e) {
            return new MeasurementTags();
        }
        panelTag = new MeasurementTags();
        if (props.containsKey("tag_key")) {
            panelTag.setKey(props.get("tag_key").toString());
            if (props.containsKey("tag_value"))
                panelTag.setValue(props.get("tag_value").toString());
        }
        return panelTag;
    }

    private MeasurementTags getTileTag(InformationTile tile) {
        MeasurementTags panelTag;
        Map<String, Object> props = null;
        try {
            props = Json.parse(tile.getProps()).toMap();
        } catch (ParseException e) {
            return new MeasurementTags();
        }
        panelTag = new MeasurementTags();
        if (props.containsKey("tag_key")) {
            panelTag.setKey(props.get("tag_key").toString());
            if (props.containsKey("tag_value"))
                panelTag.setValue(props.get("tag_value").toString());
        }
        return panelTag;
    }

//    public List<InformationTileMeasurement> getPanelMeasurements(long id) {
//        InformationPanel panel = informationPanelRepository.findById(id).orElse(null);
//
//        if (panel != null) {
//            return panel.getTiles().stream().map(tile ->
//
//                    tile.getInformationTileMeasurements()
//                            .stream().filter(Objects::nonNull)
//                            .collect(Collectors.toList())
//            ).flatMap(List::stream).collect(Collectors.toList());
//        }
//        throw new NotFoundException("No panel found related with id " + id);
//    }

//    public List<Measurement> getTileMeasurements(long tileId, Long assetId, Long userId,
//                                                 MeasurementTags panelTag, MeasurementTags mTag) {
//
//        InformationTile tile = informationTileRepository.findById(tileId).orElse(null);
//        //TODO: check if user has access to the panel
//        if (tile != null) {
//            if (assetId != null) {
//                Boolean isTemplate = tile.getInformationPanel().getIsTemplate();
//                //TODO: simplify it
//                return tile.getInformationTileMeasurements().stream().map(
//                                tileM -> tileM.getMeasurement() == null
//                                        && isTemplate
//                                        && tileM.getAssetCategory() == null ?
//                                        this.getInferredMeasurements(tileM, assetId, tileM.getFunction(), panelTag, mTag).stream().map(
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

//    private List<MeasurementDAOResponse> getInferredMeasurements(InformationTileMeasurement tileM,
//                                                                 Long assetId, String func, MeasurementTags panelTag,
//                                                                 MeasurementTags mTag) {
//        return measurementRepository
//                .inferMeasurement(assetId,
//                        tileM.getMeasurementName(),
//                        tileM.getSensorName(),
//                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
//                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
//                        tileM.getType() != null ? tileM.getType().getId() : null,
//                        tileM.getPhysicalName(), mTag.getKey(), mTag.getValue(), panelTag.getKey(), panelTag.getValue())
//                .stream().map(x -> MeasurementDAOResponse.create(x,
//                        measurementDetailsRepository.findByMeasurementId(x.getId()), func))
//                .collect(Collectors.toList());
//    }
//
//    private List<MeasurementDAOResponse> getInferredMeasurements(InformationTileMeasurement tileM,
//                                                                 Long assetId, String func) {
//
//        List<MeasurementDAOResponse> list = measurementRepository
//                .findByAssetIdAndBySensorNameAndDomainAndDirectionAndType(assetId,
//                        tileM.getMeasurementName(),
//                        tileM.getSensorName(),
//                        tileM.getDomain() != null ? tileM.getDomain().name() : null,
//                        tileM.getDirection() != null ? tileM.getDirection().name() : null,
//                        tileM.getType()!=null?tileM.getType().getId():null,
//                        tileM.getPhysicalName(), )
//                .stream().map(x -> MeasurementDAOResponse.create(x,
//                        measurementDetailsRepository.findByMeasurementId(x.getId()),func))
//                .collect(Collectors.toList());
//
//        if (list.isEmpty())
//            throw new NotFoundException(
//               "No measurements related with the asset " + assetId + " with the tile data found");
//
//        return list;
//    }

//    private MeasurementDAOResponse getMeasurementFromTileMeasurement(InformationTileMeasurement tileM) {
//        if (tileM.getMeasurement() == null) {
//            return null;
//        }
//        List<MeasurementDetails> details = measurementDetailsRepository.findByMeasurementId(tileM.getMeasurement().getId());
//        return MeasurementDAOResponse.create(tileM.getMeasurement(), details, tileM.getFunction());
//    }

    /**
     * check if the input measurement type matches database
     *
     * @param dao
     * @return
     */
    private MeasurementType verifyMeasurementType(MeasurementType dao) {
        MeasurementType dbType = dao.getId() != null ? measurementTypeRepository.findById(dao.getId()).orElse(null) : null;
        if (dbType == null
                || (dbType.getName() == null && dao.getName() != null)
                || !dbType.getName().equals(dao.getName())
                || (dbType.getPhysicalName() == null && dao.getPhysicalName() != null)
                || !dbType.getPhysicalName().equals(dao.getPhysicalName())
                || (dbType.getUnit() == null && dao.getUnit() != null)
                || !dbType.getUnit().equals(dao.getUnit())
        ) {
            List<MeasurementType> types =
                    measurementTypeRepository.findMeasurementType(dao.getName(),
                            dao.getUnit(), dao.getPhysicalName());
            if (types.size() != 1) {
                throw new InvalidCreationIdAlreadyDefinedException("Measurement type does not exists");
            }
            return types.get(0);
        }
        return dbType;
    }
}
