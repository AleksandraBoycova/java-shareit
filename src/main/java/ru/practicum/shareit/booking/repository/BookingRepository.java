package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId);

    List<Booking> findAllByBookerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime before, LocalDateTime after);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, LocalDateTime before, BookingState status);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingState status);

    List<Booking> findAllByBookerIdAndStatusInAndStartAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime after);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);
}
