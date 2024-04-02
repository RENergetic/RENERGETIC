package com.renergetic.common.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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
