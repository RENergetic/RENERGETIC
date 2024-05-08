package com.renergetic.common.dao;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetRule;
import com.renergetic.common.model.Measurement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AssetRuleMultiListWithAssetsDAO {
    private SimpleAssetDAO asset;
    private List<AssetRuleDAO> assetRules;
}
