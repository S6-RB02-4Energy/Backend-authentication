package com.Energy.BasicSpringAPI.entity;

import com.Energy.BasicSpringAPI.enumerators.Roles;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * User Entity Configuration for the ORM
 * Uses Lombok to generate Getters and Setters
 *
 * @class UserEntity
 */
@Entity
@Getter
@Setter
@Table(name="users")
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @Type(type = "uuid-char")
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    @NonNull
    public String username;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column()
    public Roles role;

    @Column()
    public String confirmationCode;

    @Column(nullable = false, columnDefinition = "Boolean default false")
    public Boolean emailConfirmed;

    @Column()
    public String address;

    @Column()
    public boolean userConsent;

    public UserEntity() {
    }

    public UserEntity(UUID id, @NonNull String username, String email, String password, Roles role, String confirmationCode, Boolean emailConfirmed) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.confirmationCode = confirmationCode;
        this.emailConfirmed = emailConfirmed;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
