package com.Energy.BasicSpringAPI;

import com.Energy.BasicSpringAPI.DTO.UserInfoDto;
import com.Energy.BasicSpringAPI.controller.AuthController;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.service.AuthService;
import com.Energy.BasicSpringAPI.service.AuthenticationFilter;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationFilter authenticationFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private MailService mailService;

    @Test
    public void LoginTest() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.username = "u1";
        user1.email = "user@example.com";
        user1.password = AuthenticationFilter.getBcryptHash("qawsedrf");
        when(authService.getUser(user1.email, user1.password)).thenReturn(Optional.of(user1));
        when(authenticationFilter.createJWT(user1.getId().toString(), user1.email,user1.username, -1)).thenReturn("fakeToken");
        when(authenticationFilter.validateToken("fakeToken")).thenReturn(true);
        String requestJson = "{\n" +
                "        \"email\":\"" +
                user1.email +
                "\",        \"username\":\"u1\",\n" +
                "        \"password\": " + "\"" + user1.password + "\"" + "        \n" +
                "}";
        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("fakeToken"));
    }

    @Test
    public void verifyTokenTest() throws Exception {
       String token = "fakeToken";
       when(authenticationFilter.validateToken("fakeToken")).thenReturn(true);
        this.mockMvc.perform(post("/auth/verifyToken").contentType(MediaType.APPLICATION_JSON)
                        .content(token)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("The token is valid"));
    }

    @Test
    public void registerTest() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.password = "qawsedrf";
        user1.username = "u1";
        user1.email = "user@example.com";
        user1.role = Roles.CONSUMER;
        when(userService.existsByUsername(user1.username)).thenReturn(false);
        when(userService.existsByEmail(user1.email)).thenReturn(false);
        UserInfoDto userInfoDto = new UserInfoDto(user1.getId(), user1.username, user1.email, user1.role, user1.emailConfirmed);
        when(userService.saveUser(user1)).thenReturn(userInfoDto);
        String requestJson = "{\n" +
                "        \"email\":\"" + user1.email + "\"," + "\n" +
                "       \"username\":\"u1\",\n" +
                "        \"password\": " + "\"" + user1.password + "\"" + ",        \n" +
                "        \"role\": " + "\"" + user1.role + "\"" + "        \n" +
                "}";
        this.mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)).andDo(print()).andExpect(status().isCreated());

    }



}
