package com.renergetic.backdb.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "uuid")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class UUID {
	@Id
	private String id;
	
	@PrePersist
	private void generateID() {
		this.setId(java.util.UUID.randomUUID().toString());
	}
}
