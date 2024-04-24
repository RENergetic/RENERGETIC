package com.renergetic.common.dao.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PagedDAO<T>{
    @JsonProperty(required = true)
    private Long total;
    @JsonProperty(required = true)
    private List<T> data;
}
