package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemController.class})
class ItemControllerTest extends BaseTest {
    @Autowired
    private MockMvc         mockMvc;
    @Autowired
    private ObjectMapper    mapper;
    @MockBean
    private ItemServiceImpl service;


    @Test
    void create() throws Exception {
        when(service.create(any(), anyLong())).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(post("/items")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createUserNotFound() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/items")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void update() throws Exception {
        when(service.update(anyLong(), any(), anyLong())).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(patch("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateUserNotFound() throws Exception {
        when(service.update(anyLong(), any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(patch("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void updateItemNotFound() throws Exception {
        when(service.update(anyLong(), any(), anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(patch("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void updateUnauthorized() throws Exception {
        when(service.update(anyLong(), any(), anyLong())).thenThrow(new UnauthorizedException("User can not update this item!"));

        mockMvc.perform(patch("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Нет доступа"));
    }

    @Test
    void delete() throws Exception {
        when(service.delete(anyLong(), anyLong())).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void deleteUserNotFound() throws Exception {
        when(service.delete(anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void deleteItemNotFound() throws Exception {
        when(service.delete(anyLong(), anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void deleteUnauthorized() throws Exception {
        when(service.delete(anyLong(), anyLong())).thenThrow(new UnauthorizedException("User can not delete this item!"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Нет доступа"));
    }

    @Test
    void getById() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getByIdItemNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getByIdUserNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getAll() throws Exception {
        ItemDto item1 = buildItemDto(11L, "Бензопила", "Бензопила \"Дружба\"", true);
        item1.setNextBooking(buildBookingDto(2L, 11L, 3L, getDateFromString("2023-04-06 12:00:00"), getDateFromString("2023-04-08 11:00:00"), BookingState.APPROVED));
        item1.setLastBooking(buildBookingDto(9L, 11L, 7L, getDateFromString("2023-04-03 12:00:00"), getDateFromString("2023-04-04 11:00:00"), BookingState.APPROVED));

        ItemDto item2 = buildItemDto(17L, "Молоток", "Молоток новый", true);
        item2.setNextBooking(buildBookingDto(5L, 11L, 36L, getDateFromString("2023-04-11 12:00:00"), getDateFromString("2023-04-12 11:00:00"), BookingState.APPROVED));

        when(service.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item1, item2));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .param("from", "0")
                        .param("size", "15")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].name").value("Бензопила"))
                .andExpect(jsonPath("$[0].description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$[0].lastBooking.id").value(9))
                .andExpect(jsonPath("$[0].lastBooking.itemId").value(11))
                .andExpect(jsonPath("$[0].lastBooking.bookerId").value(7))
                .andExpect(jsonPath("$[0].lastBooking.start").value("2023-04-03T12:00:00"))
                .andExpect(jsonPath("$[0].lastBooking.end").value("2023-04-04T11:00:00"))
                .andExpect(jsonPath("$[0].nextBooking.id").value(2))
                .andExpect(jsonPath("$[0].nextBooking.itemId").value(11))
                .andExpect(jsonPath("$[0].nextBooking.bookerId").value(3))
                .andExpect(jsonPath("$[0].nextBooking.start").value("2023-04-06T12:00:00"))
                .andExpect(jsonPath("$[0].nextBooking.end").value("2023-04-08T11:00:00"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(17))
                .andExpect(jsonPath("$[1].name").value("Молоток"))
                .andExpect(jsonPath("$[1].description").value("Молоток новый"))
                .andExpect(jsonPath("$[1].nextBooking.id").value(5))
                .andExpect(jsonPath("$[1].nextBooking.itemId").value(11))
                .andExpect(jsonPath("$[1].nextBooking.bookerId").value(36))
                .andExpect(jsonPath("$[1].nextBooking.start").value("2023-04-11T12:00:00"))
                .andExpect(jsonPath("$[1].nextBooking.end").value("2023-04-12T11:00:00"))
                .andExpect(jsonPath("$[1].lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[1].available").value(true));
    }

    @Test
    void search() throws Exception {
        ItemDto item1 = buildItemDto(11L, "Бензопила", "Бензопила \"Дружба\" новая, в упаковке", true);
        ItemDto item2 = buildItemDto(17L, "Молоток", "Молоток в упаковке", true);

        when(service.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(item1, item2));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "в упаковке")
                        .param("from", "0")
                        .param("size", "15")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].name").value("Бензопила"))
                .andExpect(jsonPath("$[0].description").value("Бензопила \"Дружба\" новая, в упаковке"))
                .andExpect(jsonPath("$[0].nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(17))
                .andExpect(jsonPath("$[1].name").value("Молоток"))
                .andExpect(jsonPath("$[1].description").value("Молоток в упаковке"))
                .andExpect(jsonPath("$[1].nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[1].lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[1].available").value(true));
    }

    @Test
    void searchNoResults() throws Exception {
        when(service.search(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "15")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any())).thenReturn(buildCommentDto(1L, "Бензопила отличная", LocalDateTime.now(), "user123"));

        mockMvc.perform(post("/items/12/comment")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Бензопила отличная"))
                .andExpect(jsonPath("$.authorName").value("user123"));
    }

    @Test
    void addCommentUserNotFound() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/items/12/comment")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void addCommentItemNotFound() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(post("/items/12/comment")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void addCommentValidationError() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any())).thenThrow(new ValidationException("Empty comment"));

        mockMvc.perform(post("/items/1/comment")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Empty comment"));
    }
}