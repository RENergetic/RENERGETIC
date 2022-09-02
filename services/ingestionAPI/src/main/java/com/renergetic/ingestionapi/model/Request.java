package com.renergetic.ingestionapi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ingestion_api_request")
@Getter
@Setter
@ToString
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "method", nullable = false, insertable = true, updatable = true, unique = false)
	private String method;
	
	@Column(name = "origin", nullable = false, insertable = true, updatable = true, unique = false)
	private String origin;
	
	@Column(name = "path", nullable = false, insertable = true, updatable = true, unique = false)
	private String path;
	
	@Column(name = "date", nullable = false, insertable = true, updatable = true, unique = false)
	private LocalDateTime date;
	
	@Column(name = "size", nullable = true, insertable = true, updatable = true, unique = false)
	private Integer size;
	
	@Column(name = "status", nullable = false, insertable = true, updatable = true, unique = false)
	private Integer status;
	
	@OneToMany(mappedBy = "request")
	@JsonManagedReference
	private List<RequestError> errors;
	
	public Request() {
		this.method = "GET";
		this.origin = "";
		this.path = "";
		this.date = LocalDateTime.now();
		this.size = 0;
		this.status = 200;
		this.errors = new ArrayList<>();
	}
	
	public Request(String method, String origin, String path, Integer size, Integer status) {
		this.method = method;
		this.origin = origin;
		this.path = path;
		this.date = LocalDateTime.now();
		this.size = size;
		this.status = status;
		this.errors = new ArrayList<>();
	}
}
