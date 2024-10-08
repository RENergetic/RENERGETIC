package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.MeasurementType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class WrapperResponseDAO {
    @JsonProperty(required = false)
    private List<SimpleAssetDAO> assets;

    @JsonProperty(required = false, value = "asset_panels")
    private List<AssetPanelDAO> assetPanels;

    @JsonProperty(required = false)
    private DataDAO data;

    @JsonProperty(required = false)
    private List<DemandScheduleDAO> demands = Collections.emptyList();

    @JsonProperty(required = false)
    private List<DemandScheduleDAO> demandsPast = Collections.emptyList();

    @JsonProperty(required = false)
    private List<DemandScheduleDAO> demandsFuture = Collections.emptyList();

    @JsonProperty(required = false)
    private List<InformationPanelDAOResponse> panels;

    //TODO: dashboards
    @JsonProperty(required = false)
    private List<DashboardDAO> dashboards;
    @JsonProperty(required = false, value = "measurement_types")
    private List<MeasurementType> measurementTypes;

    @JsonProperty(required = false, value = "asset_metakeys")
    private AssetMetaKeys assetMetaKeys;

    @JsonProperty(required = false, value = "dashboard_metakeys")
    private DashboardMetaKeys dashboardMetaKeys;

    public void appendData(DataDAO data) {
        if (this.data == null) {
            this.data = data;
        } else {
            this.data.getCurrent().putAll(data.getCurrent());
            this.data.getPrediction().putAll(data.getCurrent());
        }
    }
}
