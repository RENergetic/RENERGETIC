package com.renergetic.backdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "information_panel")
@RequiredArgsConstructor
@ToString
public class InformationPanel {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "label")
    private String label;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "owner_id")
    @JsonProperty(required = false)
    private User user;

    @Getter
    @OneToMany(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_panel_id")
    private List<InformationTile> tiles;

    public InformationPanel(Long id, String uuid, String name, String label) {
        super();
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.label = label;
    }
}
