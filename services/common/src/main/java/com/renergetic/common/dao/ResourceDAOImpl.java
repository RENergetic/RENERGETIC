package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
