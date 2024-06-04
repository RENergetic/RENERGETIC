package com.renergetic.common.dao;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.RuleAction;
import com.renergetic.common.model.RuleDefinition;
import com.renergetic.common.utilities.TimeFormatValidator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleActionDAO {
    private Long id;
    private Long assetId;
    private DemandDefinitionDAO demandDefinition;
    private String fixedDuration;

    public RuleAction mapToEntity(){
        if(fixedDuration != null)
            TimeFormatValidator.validateTimeDuration(getFixedDuration());

        RuleAction entity = new RuleAction();

        Asset asset = new Asset();
        asset.setId(getAssetId());

        entity.setId(getId());
        entity.setAsset(asset);
        if(getDemandDefinition() != null)
            entity.setDemandDefinition(getDemandDefinition().mapToEntity());
        entity.setFixedDuration(getFixedDuration());
        return entity;
    }

    public static RuleActionDAO fromEntity(RuleAction entity){
        RuleActionDAO dao = new RuleActionDAO();
        dao.setId(entity.getId());
        dao.setAssetId(entity.getAsset().getId());
        if(entity.getDemandDefinition() != null)
            dao.setDemandDefinition(DemandDefinitionDAO.create(entity.getDemandDefinition()));
        dao.setFixedDuration(entity.getFixedDuration());
        return dao;
    }
}
