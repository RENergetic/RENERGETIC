package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.common.model.details.AssetDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;

@Entity
@Table(name = "asset")
@RequiredArgsConstructor
@Getter
@Setter
//@ToString // TODO: java.lang.StackOverflowError occurs when wrapper API is called
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, unique = true)
    private String name;

    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_type_id", nullable = false, insertable = true, updatable = true)
    private AssetType type;

    @Column(name = "label", nullable = true, insertable = true, updatable = true)
    private String label;

    @OneToMany(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true)
    private List<AssetConnection> connections;

    // REFERENCES ASSET_DASHBOARD
    @ManyToMany(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinTable(
            name = "asset_dashboard",
            joinColumns = @JoinColumn(name = "asset_id", nullable = true, insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "dashboard_id"))
    private List<Dashboard> assetsDashboard;

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

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "assets")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<InformationPanel> informationPanels;

    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_category_id", nullable = true, insertable = true, updatable = true)
    private AssetCategory assetCategory;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "asset")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<AssetDetails> details;


    public Asset(String name, AssetType type, String label, String location, Asset partOfAsset, User user) {
        super();
        this.name = name;
        this.type = type;
        this.label = label;

        this.location = location;
        this.parentAsset = partOfAsset;
        this.user = user;
    }

    public static Asset initUserAsset(String keycloakName, User user, AssetType assetType, String location) {
        Asset asset = new Asset();
        asset.setName(keycloakName);
        asset.setLabel(keycloakName);
        asset.setLocation(location);
        asset.setType(assetType);
        asset.setUser(user);
        return asset;
    }
}
