package com.renergetic.ingestionapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tags")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Tags {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "key", nullable = false, insertable = true, updatable = true)
	private String key;
	
	@Column(name = "value", nullable = false, insertable = true, updatable = true)
	private String value;

	public Tags(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
}
