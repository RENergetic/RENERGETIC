package com.renergetic.kpiapi.model;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "users", indexes = {@Index(name = "uniqueIndex", columnList = "keycloak_id", unique = true)})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "keycloak_id", nullable = false, insertable = true, updatable = true, unique = true)
    private String keycloakId;


    @OneToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
    private UUID uuid;

}
