package com.renergetic.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity

@Table(name = "users", indexes = {@Index(name = "uniqueIndex", columnList = "keycloak_id", unique = true)})
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO: change to nonnullable field
    @Column(name = "keycloak_id", nullable = false, insertable = true, updatable = true, unique = true)
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
