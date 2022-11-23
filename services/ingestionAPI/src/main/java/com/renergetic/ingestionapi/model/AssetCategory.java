package com.renergetic.ingestionapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "asset_category")
@RequiredArgsConstructor
@Getter
@Setter
public class AssetCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    private String name;

    @Column(name = "label", nullable = true, insertable = true, updatable = true)
    private String label;

    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    private String description;
}
