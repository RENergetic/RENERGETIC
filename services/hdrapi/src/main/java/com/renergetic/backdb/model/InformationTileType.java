package com.renergetic.backdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "information_tile_type")
@RequiredArgsConstructor
@ToString
public class InformationTileType {
    @Id
    @JsonProperty(required = true)
    @Column(name = "id", nullable = false, insertable = true, updatable = true, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    @JsonProperty(required = true)
    private String name;

    @Column(name = "label", nullable = true, insertable = true, updatable = true)
    @JsonProperty(required = false)
    private String label;
}
