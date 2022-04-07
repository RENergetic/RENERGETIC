/*
 * This file is generated by jOOQ.
 */
package com.renergetic.backdb.model.test.tables.pojos;


import java.io.Serializable;

import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Measurement implements Serializable {

    private static final long serialVersionUID = 741229709;

    private Long   id;
    private String description;
    private String direction;
    private String icon;
    private String label;
    private String name;
    private String uuid;
    private Long   measurementTypeId;

    public Measurement() {}

    public Measurement(Measurement value) {
        this.id = value.id;
        this.description = value.description;
        this.direction = value.direction;
        this.icon = value.icon;
        this.label = value.label;
        this.name = value.name;
        this.uuid = value.uuid;
        this.measurementTypeId = value.measurementTypeId;
    }

    public Measurement(
        Long   id,
        String description,
        String direction,
        String icon,
        String label,
        String name,
        String uuid,
        Long   measurementTypeId
    ) {
        this.id = id;
        this.description = description;
        this.direction = direction;
        this.icon = icon;
        this.label = label;
        this.name = name;
        this.uuid = uuid;
        this.measurementTypeId = measurementTypeId;
    }

    @NotNull
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 255)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Size(max = 255)
    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Size(max = 255)
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Size(max = 255)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @NotNull
    @Size(max = 255)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 255)
    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public Long getMeasurementTypeId() {
        return this.measurementTypeId;
    }

    public void setMeasurementTypeId(Long measurementTypeId) {
        this.measurementTypeId = measurementTypeId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Measurement (");

        sb.append(id);
        sb.append(", ").append(description);
        sb.append(", ").append(direction);
        sb.append(", ").append(icon);
        sb.append(", ").append(label);
        sb.append(", ").append(name);
        sb.append(", ").append(uuid);
        sb.append(", ").append(measurementTypeId);

        sb.append(")");
        return sb.toString();
    }
}
