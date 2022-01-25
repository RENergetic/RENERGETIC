package com.renergetic.backdb.model.information;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.renergetic.backdb.model.Information;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "userInformation")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserInformation extends Information{
	// FOREIGN KEY FROM CONNECTION TABLE
	@Column(name = "user_id")
	private long userId;

	public UserInformation(String name, String type, String unit, long user_id, long signal) {
		super(name, type, unit, signal);
		this.userId = user_id;
	}
}
