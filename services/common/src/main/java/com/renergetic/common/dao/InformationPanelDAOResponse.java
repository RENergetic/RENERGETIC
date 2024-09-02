package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.InformationPanel;
import com.renergetic.common.utilities.Json;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InformationPanelDAOResponse implements ResourceDAO {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty()
    private String label;
    @JsonProperty(value = "is_template")
    private Boolean isTemplate;
    @JsonProperty(value = "featured")
    private Boolean featured;
    @JsonProperty(value = "priority")
    private Integer priority;

    @JsonProperty(value = "props")
    private Map<String, ?> props;
    @JsonProperty()
    private List<InformationTileDAOResponse> tiles;

    public static InformationPanelDAOResponse create(InformationPanel entity) {
        return create(entity, null);
    }

    public static InformationPanelDAOResponse create(InformationPanel entity,
                                                     List<InformationTileDAOResponse> informationTileDAOResponses) {
        if (entity == null)
            return null;
        InformationPanelDAOResponse dao = new InformationPanelDAOResponse();
        dao.setId(entity.getId());
        dao.setName(entity.getName());
        dao.setLabel(entity.getLabel());
        dao.setIsTemplate(entity.getIsTemplate());
        dao.setFeatured(entity.getFeatured());
        dao.setPriority(entity.getPriority());
        if (entity.getProps() != null) {
            try {
                dao.setProps(Json.parse(entity.getProps()).toMap());
            } catch (ParseException e) {
                //tODO: verify catch
                dao.setProps(new HashMap<>());
            }
        }
        if (informationTileDAOResponses == null && entity.getTiles() != null)
            dao.setTiles(
                    entity.getTiles().stream().map(InformationTileDAOResponse::create).collect(Collectors.toList()));
        else dao.setTiles(informationTileDAOResponses);
        return dao;
    }

//    public InformationPanel mapToEntity() {
//        InformationPanel panel = new InformationPanel();
//        panel.setId(this.id);
//        panel.setName(this.name);
//        panel.setLabel(this.label);
//        panel.setIsTemplate(this.isTemplate);
//        panel.setFeatured(this.featured);
//        panel.setProps(Json.toJson(this.props));
//        if(this.tiles!=null && this.tiles.size()>0){
////            TODO: map dao tile to sql model
//            panel.setTiles(
//                    tiles.stream().map( it->it.)
//            );
//        }
////        panel.setTiles();
//
//        //set user ?
//        return panel;
//    }
}
