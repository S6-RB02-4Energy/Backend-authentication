package com.Energy.BasicSpringAPI.entity;

import javax.persistence.*;

@Entity
@Table(name="user")
public class userEntity {
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Long getId() {
        return id;
    }
}
