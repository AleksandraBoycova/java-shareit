package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, long userId) throws Exception;

    BookingDto update(long bookingId, BookingDto bookingDto, long userId);

    BookingDto getById(long id, long userId) throws UserNotFoundException, BookingNotFoundException;

    List<BookingDto> getAll(long userId, String status);

}
