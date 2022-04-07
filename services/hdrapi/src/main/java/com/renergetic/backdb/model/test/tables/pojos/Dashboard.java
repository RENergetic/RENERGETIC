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
public class Dashboard implements Serializable {

    private static final long serialVersionUID = -913494725;

    private Long   id;
    private String label;
    private String name;
    private String url;
    private Long   userId;

    public Dashboard() {}

    public Dashboard(Dashboard value) {
        this.id = value.id;
        this.label = value.label;
        this.name = value.name;
        this.url = value.url;
        this.userId = value.userId;
    }

    public Dashboard(
        Long   id,
        String label,
        String name,
        String url,
        Long   userId
    ) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.url = url;
        this.userId = userId;
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

    @Size(max = 255)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 255)
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Dashboard (");

        sb.append(id);
        sb.append(", ").append(label);
        sb.append(", ").append(name);
        sb.append(", ").append(url);
        sb.append(", ").append(userId);

        sb.append(")");
        return sb.toString();
    }
}
