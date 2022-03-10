package com.Energy.BasicSpringAPI.entity;

import com.Energy.BasicSpringAPI.enumerators.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    public Long id;
    @Column()
    public String username;
    @Column()
    public String email;
    @Column()
    public String password;
    @Column()
    public Roles role;
}
