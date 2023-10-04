package com.renergetic.hdrapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.ResourceDAO;
import lombok.*;

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
