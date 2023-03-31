package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, long userId) throws Exception;

    BookingDto update(long bookingId, BookingDto bookingDto, long userId, Boolean approved) throws Exception;

    BookingDto getById(long id, long userId) throws Exception;

    List<BookingDto> getAll(long userId, String status) throws Exception;

    List<BookingDto> getItemsForUser(long userId, String status) throws Exception;

}
