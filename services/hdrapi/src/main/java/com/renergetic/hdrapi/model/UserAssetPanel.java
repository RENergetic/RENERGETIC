package com.renergetic.hdrapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/*
private dashboards
 */
//@Entity
//@Table(name = "user_asset_panel")
//@RequiredArgsConstructor
//@Getter
//@Setter
//@ToString
//public class UserAssetPanel {
//	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "user_id", nullable = false, insertable = true, updatable = true)
//	private User user;
//
//	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "panel_id", nullable = false, insertable = true, updatable = true)
//	private InformationPanel panel;
//
//	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
//	private Asset asset;
//
//
//
//}
