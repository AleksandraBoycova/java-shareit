package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;

    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Item item;
    private User booker;
    private BookingState status;
}
