package com.renergetic.ruleevaluationservice.model.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.ruleevaluationservice.model.Details;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "asset_details")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AssetDetails extends Details {
	// FOREIGN KEY FROM CONNECTION TABLE
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "asset_id", nullable = false, insertable = true, updatable = true)
	@JsonIgnore()
	private Asset asset;

	public AssetDetails(String key, String value, Asset asset) {
		super(key, value);
		this.asset = asset;
	}
}