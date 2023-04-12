package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId);

    Page<Booking> findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(Long bookerId, List<BookingState> states, LocalDateTime before, LocalDateTime after, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndEndBeforeAndStatus(Long bookerId, LocalDateTime before, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStatusInAndStartAfter(Long bookerId, List<BookingState> states, LocalDateTime after, PageRequest pageRequest);

    Page<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(Long bookerId, List<BookingState> states, LocalDateTime before, LocalDateTime after, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeAndStatus(Long bookerId, LocalDateTime before, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatusInAndStartAfter(Long bookerId, List<BookingState> states, LocalDateTime after, PageRequest pageRequest);

    List<Booking> findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc(List<Long> itemId, BookingState status, LocalDateTime endBefore);

    List<Booking> findAllByItemIdInAndStatusAndStartAfterOrderByStart(List<Long> itemId, BookingState status, LocalDateTime startAfter);

    Page<Booking> findAllByItemOwnerId(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter(List<Long> itemId, BookingState status, LocalDateTime endBefore, LocalDateTime startBefore, LocalDateTime endAfter);

    List<Booking> findAllByItemIdInAndStatusAndStartAfter(List<Long> itemId, BookingState status, LocalDateTime startAfter);
}
