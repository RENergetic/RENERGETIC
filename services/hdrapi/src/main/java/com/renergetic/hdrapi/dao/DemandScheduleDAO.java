package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.DemandSchedule;
import com.renergetic.hdrapi.service.utils.DateConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DemandScheduleDAO {
    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    //    @JsonProperty("asset_id")
//    private Long assetId;
    @JsonProperty("asset")
    private SimpleAssetDAO asset;

    @JsonProperty("demand_definition")
    private DemandDefinitionDAO demandDefinition;

    @JsonProperty("demand_start")
    private Long demandStart;

    @JsonProperty("demand_stop")
    private Long demandStop;

    @JsonProperty("demand_update")
    private Long update;

    public static DemandScheduleDAO create(DemandSchedule demandSchedule) {
        DemandScheduleDAO demandScheduleDAO = new DemandScheduleDAO();
        demandScheduleDAO.setId(demandSchedule.getId());
        if (demandSchedule.getDemandDefinition() != null)
            demandScheduleDAO.setDemandDefinition(DemandDefinitionDAO.create(demandSchedule.getDemandDefinition()));
        if (demandSchedule.getAsset() != null) {
            SimpleAssetDAO simpleAssetDAO = SimpleAssetDAO.create(demandSchedule.getAsset());
            demandScheduleDAO.setAsset(simpleAssetDAO);

        }

//        demandScheduleDAO.setAssetId(demandSchedule.getAsset().getId());
        demandScheduleDAO.setDemandStart(DateConverter.toEpoch(demandSchedule.getDemandStart()));
        demandScheduleDAO.setDemandStop(DateConverter.toEpoch(demandSchedule.getDemandStop()));
        demandScheduleDAO.setUpdate(DateConverter.toEpoch(demandSchedule.getUpdate()));

        return demandScheduleDAO;
    }

    public DemandSchedule mapToEntity() {
        DemandSchedule demandSchedule = new DemandSchedule();
        demandSchedule.setId(this.getId());

        if (this.getDemandDefinition() != null)
            demandSchedule.setDemandDefinition(this.getDemandDefinition().mapToEntity());
        if (this.getAsset() != null) {
            Asset asset = new Asset();
            asset.setId(this.getAsset().getId());
            demandSchedule.setAsset(asset);
        }
        demandSchedule.setDemandStart(DateConverter.toLocalDateTime(this.getDemandStart()));
        demandSchedule.setDemandStop(DateConverter.toLocalDateTime(this.getDemandStop()));
        demandSchedule.setUpdate(DateConverter.toLocalDateTime(this.getUpdate()));

        return demandSchedule;
    }
}
