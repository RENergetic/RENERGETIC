package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "information_tile_layout")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class InformationTileLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "layout",columnDefinition="TEXT")
    private String layout;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
