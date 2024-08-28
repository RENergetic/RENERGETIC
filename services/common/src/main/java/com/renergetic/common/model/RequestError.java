package com.renergetic.common.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "ingestion_api_error")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RequestError {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title", nullable = false, insertable = true, updatable = true, unique = false)
	private String title;
	
	@Column(name = "message", columnDefinition="text", nullable = false, insertable = true, updatable = true, unique = false)
	private String message;
	
	@Column(name = "object", columnDefinition="text", nullable = true, insertable = true, updatable = true, unique = false)
	private String object;
	
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "request_id", nullable = false, insertable = true, updatable = true, unique = false)
	@JsonBackReference
	private Request request;
	
	public RequestError(String title, String message, String object) {
		this.title = title;
		this.message = message;
		this.object = object;
	}
	
	public RequestError(String title, String message, String object, Request request) {
		this.title = title;
		this.message = message;
		this.object = object;
		this.request = request;
	}
}
