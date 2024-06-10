package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationPanelDAO {
    @JsonProperty(required = false)
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;
    @JsonProperty(value = "is_template",required = true)
    private Boolean isTemplate = false;
    @JsonProperty(value = "featured")
    private Boolean featured;

    @JsonProperty(value = "props",required = false)
    private Map<String, ?> props;
    @JsonProperty()
    private List<InformationTileDAORequest> tiles;
//
//    public static InformationPanelDAO create(InformationPanel entity) {
//        return create(entity, null);
//    }

//    public static InformationPanelDAO create(InformationPanel entity,
//                                             List<InformationTileDAOResponse> informationTileDAOResponses) {
//        if (entity == null)
//            return null;
//        InformationPanelDAO dao = new InformationPanelDAO();
//        dao.setId(entity.getId());
//        dao.setName(entity.getName());
//        dao.setLabel(entity.getLabel());
//        dao.setIsTemplate(entity.getIsTemplate());
//        dao.setFeatured(entity.getFeatured());
//        if (entity.getProps() != null) {
//            try {
//                dao.setProps(Json.parse(entity.getProps()).toMap());
//            } catch (ParseException e) {
//                //tODO: verify catch
//                dao.setProps(new HashMap<>());
//            }
//        }
//        if (informationTileDAOResponses == null && entity.getTiles() != null)
//            dao.setTiles(
//                    entity.getTiles().stream().map(InformationTileDAORequest::mapToEntity).collect(Collectors.toList()));
//        else dao.setTiles(informationTileDAOResponses);
//        return dao;
//    }

    public InformationPanel mapToEntity() {
        InformationPanel panel = new InformationPanel();
        panel.setId(this.id);
        panel.setName(this.name);
        panel.setLabel(this.label);
        panel.setIsTemplate(this.isTemplate);
        panel.setFeatured(this.featured);
        panel.setProps(Json.toJson(this.props));
        if (this.tiles != null && this.tiles.size() > 0) {
//            TODO: map dao tile to sql model
            panel.setTiles(
                    tiles.stream().map(InformationTileDAORequest::mapToEntity).collect(Collectors.toList())
            );
        }

//        panel.setTiles();

        //set user ?
        return panel;
    }
}
