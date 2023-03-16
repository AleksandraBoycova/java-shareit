package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
   @PostMapping
    public BookingDto createBooking (@RequestBody BookingDto bookingDto) {
        return null;
    }

    @PatchMapping ("/{bookingId}")
    public BookingDto updateBooking (@PathVariable Long bookingId, @RequestBody BookingDto bookingDto,
                                     @RequestParam(name = "approved", required = false) Boolean approved) {
       return null;
    }

    @GetMapping ("/{bookingId}")
    public BookingDto getById (@PathVariable Long bookingId) {
       return null;
    }
    @GetMapping
    public List<BookingDto> getAllBookings (@RequestParam(name = "state", required = false) String state) {
       return null;
    }

    @GetMapping("/owner")
    public List<ItemDto> getItemsByAll (@RequestParam(name = "state", required = false) String state) {
       return null;
    }

}
