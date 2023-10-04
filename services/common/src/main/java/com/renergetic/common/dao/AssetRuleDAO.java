package com.renergetic.common.dao;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.AssetRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetRuleDAO {
    private Long id;
    private Long assetId;
    private Long measurement1Id;
    private String functionMeasurement1;
    private String timeRangeMeasurement1;
    private Long measurement2Id;
    private String functionMeasurement2;
    private String timeRangeMeasurement2;
    private boolean compareToConfigThreshold;
    private String manualThreshold;
    private String comparator;
    private boolean active;

    public AssetRule mapToEntity(Asset asset, Measurement measurement1, Measurement measurement2){
        AssetRule assetRule = new AssetRule();
        assetRule.setId(getId());
        assetRule.setAsset(asset);
        assetRule.setMeasurement1(measurement1);
        assetRule.setFunctionMeasurement1(getFunctionMeasurement1());
        assetRule.setTimeRangeMeasurement1(getTimeRangeMeasurement1());
        assetRule.setMeasurement2(measurement2);
        assetRule.setFunctionMeasurement2(getFunctionMeasurement2());
        assetRule.setTimeRangeMeasurement2(getTimeRangeMeasurement2());
        assetRule.setCompareToConfigThreshold(isCompareToConfigThreshold());
        assetRule.setManualThreshold(getManualThreshold());
        assetRule.setComparator(getComparator());
        assetRule.setActive(isActive());
        return assetRule;
    }

    public static AssetRuleDAO fromEntity(AssetRule assetRule){
        AssetRuleDAO dao = new AssetRuleDAO();
        dao.setId(assetRule.getId());
        dao.setAssetId(assetRule.getAsset() != null ? assetRule.getAsset().getId() : null);
        dao.setMeasurement1Id(assetRule.getMeasurement1() != null ? assetRule.getMeasurement1().getId() : null);
        dao.setFunctionMeasurement1(assetRule.getFunctionMeasurement1());
        dao.setTimeRangeMeasurement1(assetRule.getTimeRangeMeasurement1());
        dao.setMeasurement2Id(assetRule.getMeasurement2() != null ? assetRule.getMeasurement2().getId() : null);
        dao.setFunctionMeasurement2(assetRule.getFunctionMeasurement2());
        dao.setTimeRangeMeasurement2(assetRule.getTimeRangeMeasurement2());
        dao.setCompareToConfigThreshold(assetRule.isCompareToConfigThreshold());
        dao.setManualThreshold(assetRule.getManualThreshold());
        dao.setComparator(assetRule.getComparator());
        dao.setActive(assetRule.isActive());
        return dao;
    }
}
