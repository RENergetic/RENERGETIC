package com.renergetic.backdb.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "information_tile")
@RequiredArgsConstructor
@ToString
public class InformationTile {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    @Column(name = "layout")
    private String layout;

    @Getter
    @Setter
    @Column(name = "props")
    private String props;

    /*@Getter
    @Setter
    @Column(name = "information_panel_id")
    private Long informationPanelId;*/

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinTable(
            name = "information_tile_measurement",
            joinColumns = @JoinColumn(name = "information_tile_id"),
            inverseJoinColumns = @JoinColumn(name = "measurement_id"))
    private List<Measurement> measurements;

    @Getter
    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "information_tile_type_id", nullable = false)
    private InformationTileType type;

    public void setType(Long id) {
        if (id != null) {
            this.type = new InformationTileType();
            this.type.setId(id);
        }
    }
}
