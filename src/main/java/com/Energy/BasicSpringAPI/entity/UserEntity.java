package com.Energy.BasicSpringAPI.entity;

import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
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
@Table(name="user")
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

}
