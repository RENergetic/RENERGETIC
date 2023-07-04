package com.renergetic.common.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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
