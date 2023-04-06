package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemRequestController.class})
class ItemRequestControllerTest extends BaseTest {

    @Autowired
    private MockMvc                mockMvc;
    @Autowired
    private ObjectMapper           mapper;
    @MockBean
    private ItemRequestServiceImpl service;

    @Test
    void create() throws Exception {
        when(service.create(any(), anyLong())).thenReturn(buildItemRequestDto(2L, 2L, "Хотел бы воспользоваться мясорубкой"));
        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.requester").value(2))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться мясорубкой"));
    }

    @Test
    void createUserNotFound() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void createValidationError() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new ValidationException("Description is null"));

        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Description is null"));
    }

    @Test
    void getItemRequests() throws Exception {
        ItemRequestDto ir1 = buildItemRequestDto(2L, 2L, "Хотел бы воспользоваться мясорубкой");
        ItemRequestDto ir2 = buildItemRequestDto(4L, 2L, "Хотел бы воспользоваться бензопилой");
        ir2.setItems(List.of(buildItemDto(11L, "Бензопила", "Бензопила \"Дружба\" новая, в упаковке", true)));
        when(service.getAllOwnRequests(anyLong())).thenReturn(List.of(ir1, ir2));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header(xShareUserId, 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].requester").value(2))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться мясорубкой"))
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].requester").value(2))
                .andExpect(jsonPath("$[1].items[0].id").value(11))
                .andExpect(jsonPath("$[1].items[0].name").value("Бензопила"))
                .andExpect(jsonPath("$[1].items[0].description").value("Бензопила \"Дружба\" новая, в упаковке"))
                .andExpect(jsonPath("$[1].description").value("Хотел бы воспользоваться бензопилой"));
    }

    @Test
    void getItemRequestsUserNotFound() throws Exception {
        when(service.getAllOwnRequests(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getItemRequestUserNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/45")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getItemRequestItemRequestNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new ItemRequestNotFoundException("Item request not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/45")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getItemRequest() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenReturn(buildItemRequestDto(2L, 2L, "Хотел бы воспользоваться мясорубкой"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header(xShareUserId, 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.requester").value(2))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться мясорубкой"));
    }

    @Test
    void getAllItemRequests() throws Exception {
        ItemRequestDto ir1 = buildItemRequestDto(2L, 7L, "Хотел бы воспользоваться мясорубкой");
        ItemRequestDto ir2 = buildItemRequestDto(4L, 9L, "Хотел бы воспользоваться бензопилой");
        ir2.setItems(List.of(buildItemDto(11L, "Бензопила", "Бензопила \"Дружба\" новая, в упаковке", true)));
        when(service.getAllUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(ir1, ir2));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header(xShareUserId, 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].requester").value(7))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться мясорубкой"))
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].requester").value(9))
                .andExpect(jsonPath("$[1].items[0].id").value(11))
                .andExpect(jsonPath("$[1].items[0].name").value("Бензопила"))
                .andExpect(jsonPath("$[1].items[0].description").value("Бензопила \"Дружба\" новая, в упаковке"))
                .andExpect(jsonPath("$[1].description").value("Хотел бы воспользоваться бензопилой"));
    }

    @Test
    void getAllItemRequestsUserNotFound() throws Exception {
        when(service.getAllUserRequests(anyLong(), anyInt(), anyInt())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getAllItemRequestsValidationError() throws Exception {
        when(service.getAllUserRequests(anyLong(), anyInt(), anyInt())).thenThrow(new ValidationException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error"));
    }

}