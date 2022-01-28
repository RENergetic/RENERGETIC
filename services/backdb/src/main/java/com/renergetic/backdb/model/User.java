package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "people")
@RequiredArgsConstructor
@ToString
public class User {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Getter
	@Setter
	@Column(name = "name")
	private String name;
	
	// FOREIGN KEY FROM ASSET TABLE (ASSET WHERE USER RESIDE)
	@OneToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "reside_asset_id", nullable = true)
	private Asset resideAsset;

	public User(String name, long reside_asset_id) {
		super();
		this.name = name;
		this.resideAsset = new Asset();
		this.resideAsset.setId(reside_asset_id);
	}

	public Asset getResideAsset() {
		return resideAsset;
	}

	public void setResideAsset(Long reside_asset_id) {
		this.resideAsset = new Asset();
		this.resideAsset.setId(reside_asset_id);
	}
	
}
