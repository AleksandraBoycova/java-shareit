package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
public class ItemDto {
    private long             id;
    private String           name;
    private String           description;
    private Boolean          available;
    private Long             request;
    private List<CommentDto> comments;
    private BookingDto       lastBooking;
    private BookingDto       nextBooking;
}
