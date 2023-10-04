package com.renergetic.kpiapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

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
	private String uuid;
	
	@PrePersist
	private void generateID() {
		this.setUuid(java.util.UUID.randomUUID().toString());
	}
}
