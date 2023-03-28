package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId);

    List<Booking> findAllByBookerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime before, LocalDateTime after);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, LocalDateTime before, BookingState status);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingState status);

    List<Booking> findAllByBookerIdAndStatusInAndStartAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime after);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime before, LocalDateTime after);

    List<Booking> findAllByItemOwnerIdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, LocalDateTime before, BookingState status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingState status);

    List<Booking> findAllByItemOwnerIdAndStatusInAndStartAfterOrderByStartDesc(Long bookerId, List<BookingState> states, LocalDateTime after);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);

    Optional<Booking> findFirstByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemId, BookingState status, LocalDateTime endBefore, LocalDateTime startBefore, LocalDateTime endAfter);

    Optional<Booking> findFirstByItemIdInAndStatusAndStartAfterOrderByStartAsc(List<Long> itemId, BookingState status, LocalDateTime startAfter);

    /*

    List<Booking> allApprovedBookingsForItem = bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId())
                        && booking.getStatus().equals(BookingState.APPROVED))
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        Booking last = allApprovedBookingsForItem.stream()
                .filter(booking -> booking.getEnd().isBefore(now)
                        || (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking next = allApprovedBookingsForItem.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);



     */
}
