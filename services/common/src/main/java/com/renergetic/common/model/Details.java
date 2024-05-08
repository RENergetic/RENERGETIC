package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
