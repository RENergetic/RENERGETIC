package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.model.InformationPanel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationPanelDAOResponse {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;
    @JsonProperty(value = "is_template")
    private Boolean isTemplate;

    @JsonProperty()
    private List<InformationTileDAOResponse> tiles;

    public static InformationPanelDAOResponse create(InformationPanel entity) {

        return create(entity, entity.getTiles() != null ? entity.getTiles()
                .stream().map(InformationTileDAOResponse::create).collect(Collectors.toList()) : new ArrayList<>());

    }

    public static InformationPanelDAOResponse create(InformationPanel entity,
                                                     List<InformationTileDAOResponse> informationTileDAOResponses) {
        if (entity == null)
            return null;
        InformationPanelDAOResponse dao = new InformationPanelDAOResponse();
        dao.setId(entity.getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        dao.setTiles(informationTileDAOResponses);
        dao.setIsTemplate(entity.getIsTemplate());
        return dao;
    }
}
