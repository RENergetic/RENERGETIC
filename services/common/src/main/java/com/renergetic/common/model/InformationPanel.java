package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "information_panel")
@RequiredArgsConstructor
@Getter
@Setter
//@ToString // TODO: java.lang.StackOverflowError occurs when wrapper API is called
public class InformationPanel {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "is_template")
    private Boolean isTemplate;
    @Column(name = "featured",nullable = false )
    private Boolean featured=false;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "owner_id")
    @JsonProperty(required = false)
    private User user;

    @ManyToMany(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinTable(
			name = "asset_panel",
			joinColumns = @JoinColumn(name = "panel_id", nullable = true, insertable = true, updatable = true),
			inverseJoinColumns = @JoinColumn(name = "asset_id"))
	private List<Asset> assets;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;
	
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "informationPanel")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<InformationTile> tiles;

    @Column(name = "props", nullable = true, insertable = true, updatable = true, unique = false,columnDefinition="TEXT")
    private String props;

    public InformationPanel(Long id, String name, String label) {
        super();
        this.id = id;
        this.name = name;
        this.label = label;
    }
}
