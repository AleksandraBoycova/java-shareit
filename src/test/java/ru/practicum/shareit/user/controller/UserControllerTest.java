package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class})
class UserControllerTest extends BaseTest {

    @Autowired
    private MockMvc         mockMvc;
    @Autowired
    private ObjectMapper    mapper;
    @MockBean
    private UserServiceImpl service;

    @Test
    void create() throws Exception {
        when(service.create(any())).thenReturn(buildUserDto(3L, "user@mail.com", "user"));

        mockMvc.perform(post("/users")
                        .header(xShareUserId, 38)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void createValidationError() throws Exception {
        when(service.create(any())).thenThrow(new ValidationException("Error"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error"));
    }

    @Test
    void update() throws Exception {
        when(service.update(anyLong(), any())).thenReturn(buildUserDto(3L, "user@mail.com", "user"));

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        when(service.update(anyLong(), any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void delete() throws Exception {
        when(service.delete(anyLong())).thenReturn(buildUserDto(3L, "user@mail.com", "user"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void deleteUserNotFound() throws Exception {
        when(service.delete(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getById() throws Exception {
        when(service.getById(anyLong())).thenReturn(buildUserDto(3L, "user@mail.com", "user"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(service.getById(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                buildUserDto(1L, "user@mail.com", "user"),
                buildUserDto(2L, "user_user@mail.com", "user_user")));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user@mail.com"))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user_user@mail.com"))
                .andExpect(jsonPath("$[1].name").value("user_user"));
    }
}