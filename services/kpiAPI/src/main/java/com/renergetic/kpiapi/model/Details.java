package com.renergetic.kpiapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*@Entity
@Table(name = "information")*/
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@MappedSuperclass
public class Details {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	
	@Column(name = "key", nullable = false, insertable = true, updatable = true)
	private String key;
	
	@Column(name = "value", nullable = true, insertable = true, updatable = true)
	private String value;

	public Details(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
}
