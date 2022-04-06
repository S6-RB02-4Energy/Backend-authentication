package com.Energy.BasicSpringAPI.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String email;
    private String userName;
    private String password;
}
