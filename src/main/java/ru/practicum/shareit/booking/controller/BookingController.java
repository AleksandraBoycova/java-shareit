package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) throws Exception {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId, BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(name = "approved", required = false) Boolean approved) throws Exception {
        return bookingService.update(bookingId, bookingDto, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) throws Exception {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @Min(1) @RequestParam(value = "size", defaultValue = "20") Integer size) throws Exception {
        return bookingService.getAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                          @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @Min(1) @RequestParam(value  = "size", defaultValue = "20") Integer size) throws Exception {
        return bookingService.getItemsForUser(userId, state, from, size);
    }

}
