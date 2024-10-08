package com.renergetic.kpiapi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "kpi_constants")
public class KPIConstant {
	
	public KPIConstant(long id, double a, double b, double g, double d) {
		this.id = id;
		
		alpha = a;
		beta = b;
		gamma = g;
		delta = d;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Double alpha;
	private Double beta;
	private Double gamma;
	private Double delta;
}
