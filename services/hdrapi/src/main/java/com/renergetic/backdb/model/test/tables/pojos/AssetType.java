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
public class AssetType implements Serializable {

    private static final long serialVersionUID = 1823459358;

    private Long   id;
    private String category;
    private String label;
    private String name;
    private Long   renovable;

    public AssetType() {}

    public AssetType(AssetType value) {
        this.id = value.id;
        this.category = value.category;
        this.label = value.label;
        this.name = value.name;
        this.renovable = value.renovable;
    }

    public AssetType(
        Long   id,
        String category,
        String label,
        String name,
        Long   renovable
    ) {
        this.id = id;
        this.category = category;
        this.label = label;
        this.name = name;
        this.renovable = renovable;
    }

    @NotNull
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 255)
    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Long getRenovable() {
        return this.renovable;
    }

    public void setRenovable(Long renovable) {
        this.renovable = renovable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AssetType (");

        sb.append(id);
        sb.append(", ").append(category);
        sb.append(", ").append(label);
        sb.append(", ").append(name);
        sb.append(", ").append(renovable);

        sb.append(")");
        return sb.toString();
    }
}
