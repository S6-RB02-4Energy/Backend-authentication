package com.Energy.BasicSpringAPI;

import com.Energy.BasicSpringAPI.controller.UserController;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.*;


@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getAllUsersTest() throws Exception {
        List<UserEntity> mockList = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        mockList.add(user1);
        mockList.add(user2);
        when(service.findAll()).thenReturn(mockList);
        this.mockMvc.perform(get("/user/all")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("" +
                        "[\n" +
                        "    {\n" +
                        "        \"id\":" +
                        user1.getId()+
                        "    },\n" +
                        "    {\n" +
                        "        \"id\":" +
                        user2.getId()+
                        "    }\n" +
                        "]"));
    }


    @Test
    public void getAllUsersByRoleTest() throws Exception {
        List<UserEntity> mockList = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        user1.setRole(Roles.ADMIN);
        user1.setRole(Roles.LARGECONSUMER);
        mockList.add(user1);
        when(service.findAllByRole(Roles.ADMIN)).thenReturn(mockList);
        this.mockMvc.perform(get("/user/all/ADMIN")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("" +
                        "[\n" +
                        "    {\n" +
                        "        \"id\":" +
                        user1.getId()+
                        "    }\n"+
                        "]"));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        UserEntity user1 = new UserEntity();
        when(service.findById(user1.getId())).thenReturn(Optional.of(user1));
        this.mockMvc.perform(get("/user/id/"+user1.getId())).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(
                        "    {\n" +
                        "        \"id\":" +
                        user1.getId()+
                        "    }\n"));
    }

    @Test
    public void getUserByUsernameTest() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        when(service.findByUsername("user1")).thenReturn(Optional.of(user1));
        this.mockMvc.perform(get("/user/username/"+user1.getUsername())).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(
                        "    {\n" +
                                "        \"id\":" +
                                user1.getId() +
                                ",\n \"username\":" +
                                "\"user1\"" +
                                "    }\n"));
    }

    @Test
    public void getUserByEmailTest() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        when(service.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        this.mockMvc.perform(get("/user/email/"+user1.getEmail())).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(
                        "    {\n" +
                                "        \"id\":" +
                                user1.getId() +
                                ",\n \"email\":" +
                                "\"user1@example.com\"" +
                                "    }\n"));
    }

    @Test
    public void updateUserTest() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user@example.com");
        UserEntity user1Updated = user1;
        user1Updated.setEmail("user1@example.com");

        String requestJson = "{\n" +
                "        \"id\":\"" +
                user1.getId() +
                "\",        \"email\":\"user1@example.com\",\n" +
                "        \"emailConfirmed\": true        \n" +
                "}";
        when(service.save(user1Updated)).thenReturn(user1Updated);
        when(service.getUserById(user1.getId())).thenReturn(user1);
        this.mockMvc.perform(
                put("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "    {\n" +
                                "        \"id\":" +
                                user1.getId() +
                                ",\n \"email\":" +
                                "\"user1@example.com\"" +
                                "    }\n"));
    }
    @Test
    public void deleteUserTest() throws Exception {
        UserEntity user1 = new UserEntity();
        this.mockMvc.perform(delete("/user/delete/"+user1.getId())).andDo(print()).andExpect(status().isOk());
    }
}
