package com.renergetic.common.model;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "uuid")
@Data
public class UUID {
	@Id
	private String uuid;
	
	@PrePersist
	private void generateID() {
		this.setUuid(java.util.UUID.randomUUID().toString());
	}
}
