package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.model.InformationTile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationPanelDAOSimple {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;
    @JsonProperty()
    private Integer priority;

    public static InformationPanelDAOSimple create(InformationPanel panel) {
        var dao = new InformationPanelDAOSimple();
        dao.setId(panel.getId());
        dao.setName(panel.getName());
        dao.setLabel(panel.getLabel());
        dao.setPriority(panel.getPriority());
        return dao;
    }
}
