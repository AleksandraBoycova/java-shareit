package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingState.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest extends BaseTest {
    @Autowired
    private BookingRepository bookingRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(3);
        assertEquals(2, bookings.size());
        assertArrayEquals(new long[]{4L, 7L}, new long[]{bookings.get(0).getId(), bookings.get(1).getId()});
        List<Booking> emptyBookings = bookingRepository.findAllByBookerId(48);
        assertTrue(emptyBookings.isEmpty());

    }

    @Test
    void findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(1L, List.of(BookingState.WAITING),
                getDateFromString("2023-04-03 15:35:40"),
                getDateFromString("2023-04-03 16:24:40"),
                PageRequest.of(0, 10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(8L, bookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndEndBeforeAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(1L,
                getDateFromString("2023-04-03 16:44:40"),
                APPROVED,
                PageRequest.of(0, 10, Sort.by("start"))).getContent();
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(1L,
                BookingState.WAITING,
                PageRequest.of(0, 10,
                        Sort.by("id").descending())).getContent();
        assertEquals(2, bookings.size());
        assertEquals(8, bookings.get(0).getId());
        assertEquals(6, bookings.get(1).getId());
    }

    @Test
    void findAllByBookerIdAndStatusInAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusInAndStartAfter(3L,
                List.of(APPROVED, BookingState.WAITING),
                getDateFromString("2023-04-10 13:44:40"),
                PageRequest.of(0, 10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(7, bookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(4L, List.of(APPROVED, WAITING),
                getDateFromString("2023-04-03 17:00:00"),
                getDateFromString("2023-04-03 17:00:00"),
                PageRequest.of(0, 10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(4, bookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeAndStatus(4L,
                getDateFromString("2023-04-03 16:30:00"),
                APPROVED, PageRequest.of(0, 10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(4L,
                APPROVED, PageRequest.of(0, 10, Sort.by("start"))).getContent();
        assertEquals(2, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(4, bookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusInAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusInAndStartAfter(4L,
                List.of(APPROVED, REJECTED),
                getDateFromString("2023-04-03 15:30:00"), PageRequest.of(0, 10, Sort.by("start"))).getContent();
        assertEquals(3, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(5, bookings.get(1).getId());
        assertEquals(4, bookings.get(2).getId());
    }

    @Test
    void findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc(List.of(2L, 1L),
                APPROVED,
                getDateFromString("2023-04-03 16:30:00"));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    void findAllByItemIdInAndStatusAndStartAfterOrderByStart() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusAndStartAfterOrderByStart(List.of(2L, 1L),
                APPROVED,
                getDateFromString("2023-04-03 16:30:00"));
        assertEquals(2, bookings.size());
        assertEquals(4, bookings.get(0).getId());
        assertEquals(7, bookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(4L, PageRequest.of(0, 10, Sort.by("start"))).getContent();
        assertEquals(5, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(5, bookings.get(1).getId());
        assertEquals(6, bookings.get(2).getId());
        assertEquals(4, bookings.get(3).getId());
        assertEquals(2, bookings.get(4).getId());
    }

    @Test
    void findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter(List.of(2L, 1L, 4L),
                APPROVED,
                getDateFromString("2023-04-03 15:45:00"),
                getDateFromString("2023-04-03 16:37:00"),
                getDateFromString("2023-04-03 16:40:00"));
        assertEquals(3, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(4, bookings.get(1).getId());
        assertEquals(5, bookings.get(2).getId());
    }

    @Test
    void findAllByItemIdInAndStatusAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusAndStartAfter(List.of(2L, 1L, 4L),
                APPROVED,
                getDateFromString("2023-04-03 15:45:00"));
        assertEquals(2, bookings.size());
        assertEquals(7, bookings.get(0).getId());
        assertEquals(4, bookings.get(1).getId());
    }

}