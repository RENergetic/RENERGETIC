package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.InformationPanel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AssetPanelDAO {
    @JsonProperty(required = true)
    private AssetSimplifiedDAO asset;

    @JsonProperty(required = true)
    private PanelSimplifiedDAO panel;
    //TODO: generate panel for asset using given panel template
//    @JsonProperty(required = true)
//    private InformationPanelDAOResponse panel;

    public static AssetPanelDAO fromEntities(Asset asset, InformationPanel panel){
        AssetPanelDAO assetPanelDAO = new AssetPanelDAO();

        AssetSimplifiedDAO assetSimplifiedDAO = new AssetSimplifiedDAO();
        assetSimplifiedDAO.setId(asset.getId());
        assetSimplifiedDAO.setName(asset.getName());
        assetSimplifiedDAO.setLabel(asset.getLabel());
        assetPanelDAO.setAsset(assetSimplifiedDAO);

        PanelSimplifiedDAO panelSimplifiedDAO = new PanelSimplifiedDAO();
        panelSimplifiedDAO.setId(panel.getId());
        panelSimplifiedDAO.setName(panel.getName());
        panelSimplifiedDAO.setLabel(panel.getLabel());
        panelSimplifiedDAO.setTemplate(panel.getIsTemplate());
        assetPanelDAO.setPanel(panelSimplifiedDAO);

        return assetPanelDAO;
    }

    public static AssetPanelDAO fromDAO(AssetDAOResponse asset, InformationPanelDAOResponse panel){
        AssetPanelDAO assetPanelDAO = new AssetPanelDAO();

        AssetSimplifiedDAO assetSimplifiedDAO = new AssetSimplifiedDAO();
        assetSimplifiedDAO.setId(asset.getId());
        assetSimplifiedDAO.setName(asset.getName());
        assetSimplifiedDAO.setLabel(asset.getLabel());
        assetPanelDAO.setAsset(assetSimplifiedDAO);

        PanelSimplifiedDAO panelSimplifiedDAO = new PanelSimplifiedDAO();
        panelSimplifiedDAO.setId(panel.getId());
        panelSimplifiedDAO.setName(panel.getName());
        panelSimplifiedDAO.setLabel(panel.getLabel());
        panelSimplifiedDAO.setTemplate(panel.getIsTemplate());
        assetPanelDAO.setPanel(panelSimplifiedDAO);

        return assetPanelDAO;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class AssetSimplifiedDAO {
        @JsonProperty(required = true)
        private Long id;

        @JsonProperty(required = true)
        private String name;

        @JsonProperty(required = true)
        private String label;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class PanelSimplifiedDAO {
        @JsonProperty(required = true)
        private Long id;

        @JsonProperty(required = true)
        private String name;

        @JsonProperty(required = true)
        private String label;
        @JsonProperty(required = true,value = "is_template" )
        private boolean isTemplate;
    }
}
