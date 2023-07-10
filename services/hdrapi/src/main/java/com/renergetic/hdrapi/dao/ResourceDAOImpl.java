package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.dao.projection.ResourceDAO;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.service.utils.Json;
import lombok.*;
import org.apache.tomcat.util.json.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ResourceDAOImpl implements ResourceDAO {
    @JsonProperty()
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = false)
    private String label;

   public static ResourceDAO create(Long id,String name,String label){
        return new ResourceDAOImpl(id,name,label);
    }
    public static ResourceDAO create(Long id,String name ){
        return ResourceDAOImpl.create(id,name,null);
    }
}
