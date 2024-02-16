package com.renergetic.kubeflowapi.dao;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kubeflowapi.model.ExampleEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ExampleResponse {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty(required = true)
    private ZonedDateTime requestDate;

    @JsonProperty(required = true)
    private Long id;
	@JsonProperty(required = true)
	private String name;
	@JsonProperty(required = true)
	private String data;

    // Method to create this DTO from a JPA entity
    public static ExampleResponse create(ExampleEntity example) {
        ExampleResponse dao = new ExampleResponse();

        dao.setRequestDate(ZonedDateTime.now());
        dao.setId(example.getId());
        dao.setName(example.getName());
        dao.setData(example.getData());
        
        return dao;
    }


}
