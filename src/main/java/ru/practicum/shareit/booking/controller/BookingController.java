package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking (@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) throws Exception {
       return bookingService.create(bookingDto, userId);
    }

    @PatchMapping ("/{bookingId}")
    public BookingDto updateBooking (@PathVariable Long bookingId, @RequestBody BookingDto bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(name = "approved", required = false) Boolean approved) throws UserNotFoundException, BookingNotFoundException, UnauthorizedException, ItemNotFoundException {
       return bookingService.update(bookingId, bookingDto, userId, approved);
    }

    @GetMapping ("/{bookingId}")
    public BookingDto getById (@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) throws UserNotFoundException, BookingNotFoundException {
       return bookingService.getById(bookingId, userId);
    }
    @GetMapping
    public List<BookingDto> getAllBookings (@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(name = "state", required = false) String state) throws ValidationException {
       return bookingService.getAll(userId, state);
    }

    @GetMapping("/owner")
    public List<ItemDto> getItemsByAll (@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(name = "state", required = false) String state) throws ValidationException {
       return bookingService.getItemsForUser(userId, state);
    }

}
