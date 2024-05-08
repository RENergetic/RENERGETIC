package com.renergetic.ingestionapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Subselect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Subselect("select * from tags")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Tags {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "key", nullable = false, insertable = true, updatable = true)
	private String key;
	
	@Column(name = "value", nullable = false, insertable = true, updatable = true)
	private String value;
	
	@Formula("(select m.measurement_id from measurement_tags m where m.tag_id = id limit 1)")
	private Long measurementId;

	public Tags(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
}
