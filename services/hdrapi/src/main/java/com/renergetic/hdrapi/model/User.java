package com.renergetic.hdrapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity

@Table(name = "users", indexes = {@Index(name = "uniqueIndex", columnList = "keycloak_id", unique = true)})
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //TODO: change to nonnullable field
    @Column(name = "keycloak_id", nullable = true, insertable = true, updatable = true, unique = true)
    private String keycloakId;


    @OneToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
    private UUID uuid;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinTable(
            name = "user_notification",
            joinColumns = @JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "notification_id"))
    private List<NotificationSchedule> userNotification;


    public User(String name) {
        super();
    }

}
