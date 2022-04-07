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
public class InformationPanel implements Serializable {

    private static final long serialVersionUID = 1721589825;

    private Long   id;
    private String label;
    private String name;
    private String uuid;
    private Long   ownerId;

    public InformationPanel() {}

    public InformationPanel(InformationPanel value) {
        this.id = value.id;
        this.label = value.label;
        this.name = value.name;
        this.uuid = value.uuid;
        this.ownerId = value.ownerId;
    }

    public InformationPanel(
        Long   id,
        String label,
        String name,
        String uuid,
        Long   ownerId
    ) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.uuid = uuid;
        this.ownerId = ownerId;
    }

    @NotNull
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InformationPanel (");

        sb.append(id);
        sb.append(", ").append(label);
        sb.append(", ").append(name);
        sb.append(", ").append(uuid);
        sb.append(", ").append(ownerId);

        sb.append(")");
        return sb.toString();
    }
}
