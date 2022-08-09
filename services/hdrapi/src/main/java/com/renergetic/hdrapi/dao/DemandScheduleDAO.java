package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.DemandSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DemandScheduleDAO {
	@JsonProperty(access = Access.READ_ONLY)
    private Long id;

    @JsonProperty("asset_id")
    private Long assetId;

    @JsonProperty("demand_definition")
    private DemandDefinitionDAO demandDefinition;

    @JsonProperty("demand_start")
    private LocalDateTime demandStart;

    @JsonProperty("demand_stop")
    private LocalDateTime demandStop;

    @JsonProperty("demand_update")
    private LocalDateTime update;

    public static DemandScheduleDAO create(DemandSchedule demandSchedule){
        DemandScheduleDAO demandScheduleDAO = new DemandScheduleDAO();
        demandScheduleDAO.setId(demandSchedule.getId());
        if(demandSchedule.getDemandDefinition() != null)
            demandScheduleDAO.setDemandDefinition(DemandDefinitionDAO.create(demandSchedule.getDemandDefinition()));
        demandScheduleDAO.setAssetId(demandSchedule.getAsset().getId());
        demandScheduleDAO.setDemandStart(demandSchedule.getDemandStart());
        demandScheduleDAO.setDemandStop(demandSchedule.getDemandStop());
        demandScheduleDAO.setUpdate(demandSchedule.getUpdate());

        return demandScheduleDAO;
    }

    public DemandSchedule mapToEntity() {
        DemandSchedule demandSchedule = new DemandSchedule();
        demandSchedule.setId(this.getId());
        
        if(this.getDemandDefinition() != null)
            demandSchedule.setDemandDefinition(this.getDemandDefinition().mapToEntity());
        if(this.getAssetId() != null){
            Asset asset = new Asset();
            asset.setId(this.getAssetId());
            demandSchedule.setAsset(asset);
        }
        demandSchedule.setDemandStart(this.getDemandStart());
        demandSchedule.setDemandStop(this.getDemandStop());
        demandSchedule.setUpdate(this.getUpdate());

        return demandSchedule;
    }
}
