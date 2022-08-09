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
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(value = "action_type", required = true)
    private String actionType;

    @JsonProperty(required = true)
    private String action;

    @JsonProperty(required = false)
    private String message;
    
    @JsonProperty(value = "information_tile_id", required = false)
    private Long informationTileId;

    private String ext;

    public static DemandDefinitionDAO create(DemandDefinition demandDefinition){
        DemandDefinitionDAO demandDefinitionDAO = new DemandDefinitionDAO();
        demandDefinitionDAO.setId(demandDefinition.getId());
        
        if (demandDefinition.getActionType() != null)
        	demandDefinitionDAO.setActionType(demandDefinition.getActionType().toString());
        if (demandDefinition.getAction() != null)
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
        
        if (this.getActionType() != null)
        	demandDefinition.setActionType(DemandDefinitionActionType.valueOf(this.getActionType()));
        if (this.getAction() != null)
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
