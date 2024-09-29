package com.renergetic.wrapperapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.mapper.InformationTilePanelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.AssetCategoryDAO;
import com.renergetic.common.dao.AssetMetaKeys;
import com.renergetic.common.dao.AssetPanelDAO;
import com.renergetic.common.dao.AssetTypeDAO;
import com.renergetic.common.dao.DashboardDAO;
import com.renergetic.common.dao.DashboardMetaKeys;
import com.renergetic.common.dao.DataDAO;
import com.renergetic.common.dao.DemandScheduleDAO;
import com.renergetic.common.dao.InformationPanelDAOResponse;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.SimpleAssetDAO;
import com.renergetic.common.dao.WrapperRequestDAO;
import com.renergetic.common.dao.WrapperResponseDAO;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.repository.AssetCategoryRepository;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.AssetTypeRepository;
import com.renergetic.common.repository.DashboardRepository;
import com.renergetic.common.repository.DemandScheduleRepository;
import com.renergetic.common.repository.InformationPanelRepository;
import com.renergetic.common.repository.MeasurementTypeRepository;
import com.renergetic.wrapperapi.service.utils.DummyDataGenerator;

@Service
public class WrapperService {

    @Value("${api.generate.dummy-data}")
    private boolean generateDummy;

    @Autowired
    AssetRepository assetRepo;
    @Autowired
    MeasurementTypeRepository measurementTypeRepo;
    @Autowired
    AssetTypeRepository assetTypeRepo;
    @Autowired
    AssetCategoryRepository assetCategoryRepo;
    @Autowired
    InformationPanelRepository informationPanelRepo;
    @Autowired
    DashboardRepository dashboardRepo;
    @Autowired
    DemandScheduleRepository demandScheduleRepo;
    @Autowired
    DataService dataSv;

    @Autowired
    private InformationTilePanelMapper informationTilePanelMapper;
    @Autowired
    DummyDataGenerator dummyDataGenerator;

    public WrapperResponseDAO createWrapperResponse(Long userId, WrapperRequestDAO body) {

        WrapperResponseDAO wrapperResponseDAO = new WrapperResponseDAO();

        if (body.getCalls().getAssets() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getAssets();
            wrapperResponseDAO.setAssets(
                    getOwnerAssets(userId, Optional.ofNullable(data.getOffset()),
                            Optional.ofNullable(data.getLimit()))
            );
        }
        if (body.getCalls().getAssetPanels() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getAssetPanels();
            wrapperResponseDAO.setAssetPanels(
                    getPrivateAssetPanels(userId, Optional.ofNullable(data.getOffset()),
                            Optional.ofNullable(data.getLimit()))
            );
        }
        if (body.getCalls().getMeasurementTypes() != null) {
            wrapperResponseDAO.setMeasurementTypes(
                    measurementTypeRepo.findAll()
            );
        }
        if (body.getCalls().getData() != null) {
            // TODO: consider get data related to panelId
        }
        if (body.getCalls().getAssetMetaKeys() != null) {
            wrapperResponseDAO.setAssetMetaKeys(getAssetMetaKeys());
            // TODO: consider get data related to panelId 
        }

        if (body.getCalls().getPanels() != null) {
            //also called public dashboards
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getPanels();
            wrapperResponseDAO.setPanels(getPublicPanels(Optional.ofNullable(data.getLimit())));
        }
        if (body.getCalls().getDashboards() != null) {
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getDashboards();
            wrapperResponseDAO.setDashboards(
                    getDashboards(userId, Optional.ofNullable(data.getOffset()), Optional.ofNullable(data.getLimit()))
            );

            DashboardMetaKeys meta = new DashboardMetaKeys(measurementTypeRepo.findByDashboardVisibility());
            wrapperResponseDAO.setDashboardMetaKeys(meta);
        }
        if (body.getCalls().getDemands() != null) {
            //TODO: ask someone about public demands ?
            WrapperRequestDAO.PaginationArgsWrapperRequestDAO data = body.getCalls().getDemands();
            wrapperResponseDAO.setDemands(getDemandSchedules(userId, Optional.ofNullable(data.getOffset()),
                    Optional.ofNullable(data.getLimit())));
            wrapperResponseDAO.setDemandsFuture(getFutureDemandSchedules(userId));
            wrapperResponseDAO.setDemandsPast(getPastDemandSchedules(userId));

            wrapperResponseDAO.appendData(getDemandData(wrapperResponseDAO.getDemands()));
            wrapperResponseDAO.appendData(getDemandData(wrapperResponseDAO.getDemandsFuture()));
            wrapperResponseDAO.appendData(getDemandData(wrapperResponseDAO.getDemandsPast()));
        }
        return wrapperResponseDAO;
    }

    private List<SimpleAssetDAO> getOwnerAssets(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        return assetRepo.findByUserIdConnectionTypes(
                userId,
                List.of(ConnectionType.owner.toString()),
                offset.orElse(0L),
                limit.orElse(20)
        ).stream().map(SimpleAssetDAO::create).collect(Collectors.toList());
    }

    private List<AssetPanelDAO> getPrivateAssetPanels(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        List<String> connectionTypes = List.of(ConnectionType.owner.toString(), ConnectionType.resident.toString());

        return assetRepo.findByUserIdConnectionTypes(userId,
                        connectionTypes,
                        offset.orElse(0L),
                        limit.orElse(20))
                .stream()
                .map(x -> x.getInformationPanels()
                        .stream()
                        .filter(InformationPanel::getFeatured)
                        .map(y -> AssetPanelDAO.fromEntities(x, y))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    private AssetMetaKeys getAssetMetaKeys() {
        List<AssetTypeDAO> types = assetTypeRepo.findAll().stream().map(
                AssetTypeDAO::create
        ).collect(Collectors.toList());

        List<AssetCategoryDAO> categories = assetCategoryRepo.findAll().stream().map(
                AssetCategoryDAO::create
        ).collect(Collectors.toList());

        return new AssetMetaKeys(types, categories);
    }

    //
//    private List<InformationPanelDAOResponse> getPublicPanels(Optional<Integer> limit) {
//        // TODO: check the data for do it more efficent
//        return informationPanelRepo.findFeatured(false, 0, Math.min(50, limit.orElse(20)))
//            .stream()
//            .map(panel -> InformationPanelDAOResponse.create(panel, null))
//            .collect(Collectors.toList());
//    }
    private List<InformationPanelDAOResponse> getPublicPanels(Optional<Integer> limit) {
        // TODO: check the data for do it more efficent
        return informationPanelRepo.findFeatured(false, 0, Math.min(50, limit.orElse(20)))
                .stream()
                .map(panel ->
                        informationTilePanelMapper.toDTO(panel, true))
                .collect(Collectors.toList());
    }


    private List<DashboardDAO> getDashboards(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        List<DashboardDAO> dashboards = dashboardRepo.findAvailableForUserId(userId, offset.orElse(0L), limit.orElse(20))
                .stream()
                .map(DashboardDAO::create)
                .collect(Collectors.toList());

        if (dashboards.isEmpty() && generateDummy) {
            dashboards = dummyDataGenerator.getDashboards(5);
        }

        return dashboards;
    }

    private List<DemandScheduleDAO> getDemandSchedules(Long userId, Optional<Long> offset, Optional<Integer> limit) {
        List<DemandScheduleDAO> demands = demandScheduleRepo.findByUserId(userId, LocalDateTime.now(), offset.orElse(0L), limit.orElse(20))
                .stream()
                .map(DemandScheduleDAO::create)
                .collect(Collectors.toList());

        if (demands.isEmpty() && generateDummy) {
            List<DemandScheduleDAO> schedule = demandScheduleRepo.findByUserIdGroup(userId, 0, 10)
                    .stream()
                    .map(DemandScheduleDAO::create)
                    .collect(Collectors.toList());

            demands = DummyDataGenerator.getDemand(schedule);
        }
        return demands;
    }

    private List<DemandScheduleDAO> getFutureDemandSchedules(Long userId) {
        List<DemandScheduleDAO> demands = demandScheduleRepo.findByUserIdFuture24H(userId, 0L, 20)
                .stream()
                .map(DemandScheduleDAO::create)
                .collect(Collectors.toList());

        if (demands.isEmpty() && generateDummy) {
            List<DemandScheduleDAO> schedule = demandScheduleRepo.findByUserIdGroup(userId, 0, 10)
                    .stream()
                    .map(DemandScheduleDAO::create)
                    .collect(Collectors.toList());

            demands = DummyDataGenerator.getDemand(schedule);
        }
        return demands;
    }

    private List<DemandScheduleDAO> getPastDemandSchedules(Long userId) {
        List<DemandScheduleDAO> demands = demandScheduleRepo.findByUserIdPast24H(userId, 0L, 20)
                .stream()
                .map(DemandScheduleDAO::create)
                .collect(Collectors.toList());

        if (demands.isEmpty() && generateDummy) {
            List<DemandScheduleDAO> schedule = demandScheduleRepo.findByUserIdGroup(userId, 0, 10)
                    .stream()
                    .map(DemandScheduleDAO::create)
                    .collect(Collectors.toList());

            demands = DummyDataGenerator.getDemand(schedule);
        }
        return demands;
    }

    private DataDAO getDemandData(List<DemandScheduleDAO> demands) {

        List<MeasurementDAOResponse> measurements =
                demands.stream().filter(
                                it -> it.getDemandDefinition().getTile() != null
                        ).flatMap(demand -> demand.getDemandDefinition().getTile().getMeasurements().stream())
                        .collect(Collectors.toList());

        return dataSv.getData(measurements, null, Optional.empty());
        //TODO: here there might be issue with presenting the data and choosing appropriate time interval - this should be discused
        //probably data required for the demand demand should be stored as static  DataDAO JSON in RDBMS
        //if the user chooses interval it wouldnt make sense from the demand/request perspective
    }
}
