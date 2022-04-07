package com.Energy.BasicSpringAPI.DTO;

import com.Energy.BasicSpringAPI.enumerators.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class UserInfoDto {

    public UserInfoDto(UUID id, String username, String email, Roles role, Boolean emailConfirmed) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.emailConfirmed = emailConfirmed;
    }

    @Getter @Setter
    private UUID id = UUID.randomUUID();
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private Roles role;
    @Getter @Setter
    private Boolean emailConfirmed;
}
