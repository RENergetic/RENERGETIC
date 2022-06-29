package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.backdb.model.DemandDefinition;
import com.renergetic.backdb.model.DemandDefinitionAction;
import com.renergetic.backdb.model.DemandDefinitionActionType;
import com.renergetic.backdb.model.InformationTile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DemandDefinitionDAO {
    private Long id;

    @JsonProperty("action_type")
    private String actionType;

    private String action;

    @JsonProperty("information_tile_id")
    private Long informationTileId;

    private String message;

    private String ext;

    public static DemandDefinitionDAO create(DemandDefinition demandDefinition){
        DemandDefinitionDAO demandDefinitionDAO = new DemandDefinitionDAO();
        demandDefinitionDAO.setId(demandDefinition.getId());
        demandDefinitionDAO.setActionType(demandDefinition.getActionType().toString());
        demandDefinitionDAO.setAction(demandDefinition.getAction().toString());
        if(demandDefinition.getInformationTile() != null)
            demandDefinitionDAO.setInformationTileId(demandDefinition.getInformationTile().getId());
        demandDefinitionDAO.setMessage(demandDefinition.getMessage());
        demandDefinitionDAO.setExt(demandDefinition.getExt());

        return demandDefinitionDAO;
    }

    public DemandDefinition mapToEntity() {
        DemandDefinition demandDefinition = new DemandDefinition();
        demandDefinition.setId(this.getId());
        demandDefinition.setActionType(DemandDefinitionActionType.valueOf(this.getActionType()));
        demandDefinition.setAction(DemandDefinitionAction.valueOf(this.getAction()));
        if(this.getInformationTileId() != null){
            InformationTile informationTile = new InformationTile();
            informationTile.setId(this.getInformationTileId());
            demandDefinition.setInformationTile(informationTile);
        }
        demandDefinition.setMessage(this.getMessage());
        demandDefinition.setExt(this.getExt());
        return demandDefinition;
    }
}
