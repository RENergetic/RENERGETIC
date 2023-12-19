package com.renergetic.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asset_category")
@RequiredArgsConstructor
@Getter
@Setter
public class AssetCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    private String name;

    @Column(name = "label", nullable = true, insertable = true, updatable = true)
    private String label;

    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    private String description;
}
