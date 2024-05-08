package com.renergetic.common.model;

import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.dao.SimpleAssetDAO;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.AccessLevel;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "information_tile_measurement")
@Data
public class InformationTileMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "props", nullable = true, insertable = true, updatable = true)
    private String props;

    @Column(name = "measurement_name", nullable = true, insertable = true, updatable = true)
    private String measurementName;

    @Column(name = "sensor_name", nullable = true, insertable = true, updatable = true)
    private String sensorName;
    @Column(name = "physical_name", nullable = true, insertable = true, updatable = true)
    private String physicalName;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "measurement_type_id", nullable = true, insertable = true, updatable = true)
    private MeasurementType type;

    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_category_id", nullable = true, insertable = true, updatable = true)
    // TODO: ask about if we need this field
    private AssetCategory assetCategory;

    @Column(name = "direction", nullable = true, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "domain", nullable = true, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    private Domain domain;

    @Column(name = "aggregation_function", nullable = true, insertable = true, updatable = true)
    private String function;

    // FOREIGN KEY FROM MEASUREMENT TABLE
    @Getter(AccessLevel.NONE)
    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "measurement_id", nullable = true, insertable = true, updatable = true)
    private Measurement measurement;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_id", nullable = false, insertable = true, updatable = true)
    private InformationTile informationTile;

    public Measurement getMeasurement() {
        if (measurement != null) {
            measurement.setFunction(function);
        }
        return measurement;

    }

    public MeasurementDAOResponse getMeasurementDAO() {
        if (measurement != null) {
            measurement.setFunction(function);
            return MeasurementDAOResponse.create(measurement, measurement.getDetails(), function);
        }
        var dao = new MeasurementDAOResponse();
        dao.setId(null);
        if(this.asset!=null){
            dao.setAsset(SimpleAssetDAO.create(asset));
        }
        dao.setDirection(direction);
        dao.setDomain(domain);
        dao.setName(measurementName);
        dao.setSensorName(sensorName);
        dao.setType(type);
        dao.setCategory(assetCategory != null ? assetCategory.getName() : null);
        if (this.function != null)
            dao.setFunction(InfluxFunction.obtain(this.function));
        else dao.setFunction(InfluxFunction.last);
        return dao;
    }
}