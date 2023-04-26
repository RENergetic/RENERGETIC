package com.renergetic.userapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.*;

@Entity
@Table(name = "asset")
@RequiredArgsConstructor
@Getter
@Setter
//@ToString // TODO: java.lang.StackOverflowError occurs when wrapper API is called
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    private String name;

    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true)
    private AssetType type;

    @Column(name = "label", nullable = true, insertable = true, updatable = true)
    private String label;

    @Column(name = "geo_location", nullable = true, insertable = true, updatable = true)
    private String location;

    // REFERENCES THIS TABLE TO GROUP ASSETS
    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "parent_asset_id", nullable = true, insertable = true, updatable = true)
    private Asset parentAsset;

    // FOREIGN KEY FROM USERS TABLE
    //TODO: on delete set null
    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true)
    private User user;

    @OneToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
    private UUID uuid;
}
