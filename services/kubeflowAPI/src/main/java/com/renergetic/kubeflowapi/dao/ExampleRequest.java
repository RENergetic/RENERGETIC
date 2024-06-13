//package com.renergetic.kubeflowapi.dao;
//
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.renergetic.kubeflowapi.model.ExampleEntity;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@RequiredArgsConstructor
//@ToString
//public class ExampleRequest {
//
//	@JsonProperty(required = true)
//	private String name;
//	@JsonProperty(required = true)
//	private String data;
//
//	// Method to convert from this DTO to a JPA entity
//	public ExampleEntity mapToEntity() {
//		ExampleEntity entity = new ExampleEntity();
//
//		entity.setName(name);
//		entity.setData(data);
//
//		return entity;
//	}
//}
