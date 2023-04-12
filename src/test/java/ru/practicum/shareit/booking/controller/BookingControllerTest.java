package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({BookingController.class})
class BookingControllerTest extends BaseTest {

    @Autowired
    private MockMvc            mockMvc;
    @Autowired
    private ObjectMapper       mapper;
    @MockBean
    private BookingServiceImpl service;

    @Test
    void createBookingHappyFlow() throws Exception {
        when(service.create(any(), anyLong())).thenReturn(buildBookingDto(
                43L,
                17L,
                38L,
                getDateFromString("2023-04-15 15:00:00"),
                getDateFromString("2023-04-17 12:00:00"),
                BookingState.WAITING));
        mockMvc.perform(post("/bookings")
                        .header(xShareUserId, 38)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(43))
                .andExpect(jsonPath("$.start").value("2023-04-15T15:00:00"))
                .andExpect(jsonPath("$.end").value("2023-04-17T12:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.bookerId").value(38))
                .andExpect(jsonPath("$.itemId").value(17));
    }

    @Test
    void createBookingUserNotFound() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/bookings")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void createBookingItemNotFound() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(post("/bookings")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void createBookingItemNotAvailable() throws Exception {
        when(service.create(any(), anyLong())).thenThrow(new ItemNotAvailableException("Item not available"));

        mockMvc.perform(post("/bookings")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"));
    }

    @Test
    void updateBookingHappyFlow() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenReturn(buildBookingDto(
                43L,
                17L,
                38L,
                getDateFromString("2023-04-15 15:00:00"),
                getDateFromString("2023-04-17 12:00:00"),
                BookingState.REJECTED));
        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "false")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(43))
                .andExpect(jsonPath("$.start").value("2023-04-15T15:00:00"))
                .andExpect(jsonPath("$.end").value("2023-04-17T12:00:00"))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.bookerId").value(38))
                .andExpect(jsonPath("$.itemId").value(17));
    }

    @Test
    void updateBookingBookerNotFound() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "true")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void updateBookingBookingNotFound() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenThrow(new BookingNotFoundException("Booking not found"));

        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "true")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void updateBookingItemNotFound() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "true")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void updateBookingValidationError() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenThrow(new ValidationException("Status is approved."));

        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "true")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status is approved."));
    }

    @Test
    void updateBookingUnauthorized() throws Exception {
        when(service.update(anyLong(), any(), anyLong(), anyBoolean())).thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(patch("/bookings/14")
                        .param("approved", "true")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Нет доступа"));
    }

    @Test
    void getByIdBookingNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new BookingNotFoundException("Booking not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/14")
                        .header(xShareUserId, 18L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getByIdUserNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/14")
                        .header(xShareUserId, 18L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getByIdHappyFlow() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenReturn(buildBookingDto(
                43L,
                17L,
                38L,
                getDateFromString("2023-04-15 15:00:00"),
                getDateFromString("2023-04-17 12:00:00"),
                BookingState.REJECTED));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/14")
                        .header(xShareUserId, 18L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(43))
                .andExpect(jsonPath("$.start").value("2023-04-15T15:00:00"))
                .andExpect(jsonPath("$.end").value("2023-04-17T12:00:00"))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.bookerId").value(38))
                .andExpect(jsonPath("$.itemId").value(17));
    }


    @Test
    void getAllBookings() throws Exception {
        List<BookingDto> bookingDtos = List.of(
                buildBookingDto(43L, 17L, 38L,
                        getDateFromString("2023-04-15 15:00:00"),
                        getDateFromString("2023-04-17 12:00:00"),
                        BookingState.REJECTED),
                buildBookingDto(14L, 2L, 38L,
                        getDateFromString("2023-04-19 15:00:00"),
                        getDateFromString("2023-04-20 12:00:00"),
                        BookingState.APPROVED)
        );
        when(service.getAll(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(xShareUserId, 38L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(43))
                .andExpect(jsonPath("$[0].start").value("2023-04-15T15:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-04-17T12:00:00"))
                .andExpect(jsonPath("$[0].status").value("REJECTED"))
                .andExpect(jsonPath("$[0].bookerId").value(38))
                .andExpect(jsonPath("$[0].itemId").value(17))
                .andExpect(jsonPath("$[1].id").value(14))
                .andExpect(jsonPath("$[1].start").value("2023-04-19T15:00:00"))
                .andExpect(jsonPath("$[1].end").value("2023-04-20T12:00:00"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].bookerId").value(38))
                .andExpect(jsonPath("$[1].itemId").value(2));
    }

    @Test
    void getAllBookingsUserNotFound() throws Exception {
        when(service.getAll(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(xShareUserId, 18L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getItemsByAll() throws Exception {
        List<BookingDto> bookingDtos = List.of(
                buildBookingDto(43L, 17L, 38L,
                        getDateFromString("2023-04-15 15:00:00"),
                        getDateFromString("2023-04-17 12:00:00"),
                        BookingState.REJECTED),
                buildBookingDto(14L, 2L, 38L,
                        getDateFromString("2023-04-19 15:00:00"),
                        getDateFromString("2023-04-20 12:00:00"),
                        BookingState.APPROVED)
        );
        when(service.getItemsForUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(xShareUserId, 38L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(43))
                .andExpect(jsonPath("$[0].start").value("2023-04-15T15:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-04-17T12:00:00"))
                .andExpect(jsonPath("$[0].status").value("REJECTED"))
                .andExpect(jsonPath("$[0].bookerId").value(38))
                .andExpect(jsonPath("$[0].itemId").value(17))
                .andExpect(jsonPath("$[1].id").value(14))
                .andExpect(jsonPath("$[1].start").value("2023-04-19T15:00:00"))
                .andExpect(jsonPath("$[1].end").value("2023-04-20T12:00:00"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].bookerId").value(38))
                .andExpect(jsonPath("$[1].itemId").value(2));
    }

    @Test
    void getItemsByAllUserNotFound() throws Exception {
        when(service.getItemsForUser(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(xShareUserId, 18L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getItemsByAllUnauthorized() throws Exception {
        when(service.getItemsForUser(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(xShareUserId, 18L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Нет доступа"));
    }

}