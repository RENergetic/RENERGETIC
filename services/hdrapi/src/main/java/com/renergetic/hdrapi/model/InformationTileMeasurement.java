package com.renergetic.hdrapi.model;

import javax.persistence.*;

import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.model.details.MeasurementDetails;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.stream.Collectors;

@Entity
@Table(name = "information_tile_measurement")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
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
            return MeasurementDAOResponse.create(measurement, measurement.getDetails(),function);
        }
        var dao = new MeasurementDAOResponse();
        dao.setId(null);
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