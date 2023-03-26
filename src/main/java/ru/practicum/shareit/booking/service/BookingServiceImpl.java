package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private UserRepository    userRepository;
    private ItemRepository    itemRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, long userId) throws Exception {
        checkBookingDates(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookingState.WAITING);
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item   = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ItemNotFoundException("Item not found");
        }
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Item not available");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto update(long bookingId, BookingDto bookingDto, long userId, Boolean approved) throws Exception {
        User    booker  = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        Item    item    = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (approved == null && bookingDto.getStart() == null && bookingDto.getEnd() == null) {
            throw new ValidationException("Error");
        }
        if (approved != null) {
            if (!Objects.equals(item.getOwner().getId(), booker.getId())) {
                throw new BookingNotFoundException("Booking Not Found");
            }
            if (booking.getStatus().equals(BookingState.APPROVED)) {
                throw new ValidationException("Status is approved.");
            }
            booking.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        if (!Objects.equals(booking.getBooker().getId(), booker.getId())) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (bookingDto.getStart() != null) {
            booking.setStart(bookingDto.getStart());
        }
        if (bookingDto.getEnd() != null) {
            booking.setEnd(bookingDto.getEnd());
        }
        booking.setStatus(BookingState.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long id, long userId) throws Exception {
        User    booker  = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (booking.getItem().getOwner().equals(booker) || booking.getBooker().equals(booker)) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new BookingNotFoundException("Not found for booker");
    }

    @Override
    public List<BookingDto> getAll(long userId, String status) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Predicate<Booking> filter = getBookingPredicateByStatus(status, booking -> booking.getBooker().getId() == userId);

        return bookingRepository.findAll().stream()
                .filter(filter)
                .map(BookingMapper::toBookingDto)
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .collect(Collectors.toList());
    }

    private Predicate<Booking> getBookingPredicateByStatus(String status, Predicate<Booking> filter) throws ValidationException {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "CURRENT":
                filter = filter.and(booking -> booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now)
                        && (booking.getStatus().equals(BookingState.APPROVED) || booking.getStatus().equals(BookingState.WAITING))
                        || booking.getStatus().equals(BookingState.REJECTED));
                break;
            case "PAST":
                filter = filter.and(booking -> booking.getStatus().equals(BookingState.APPROVED)
                        && booking.getEnd().isBefore(now));
                break;
            case "WAITING":
                filter = filter.and(booking -> booking.getStatus().equals(BookingState.WAITING));
                break;
            case "REJECTED":
                filter = filter.and(booking -> booking.getStatus().equals(BookingState.REJECTED));
                break;
            case "FUTURE":
                filter = filter.and(booking -> booking.getStart().isAfter(now)
                        && (booking.getStatus().equals(BookingState.APPROVED) || booking.getStatus().equals(BookingState.WAITING)));
                break;
            case "ALL":
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return filter;
    }

    @Override
    public List<BookingDto> getItemsForUser(long userId, String status) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (itemRepository.findAll().stream().noneMatch(item -> item.getOwner().getId().equals(userId))) {
            throw new UnauthorizedException("Unauthorized");
        }
        Predicate<Booking> filter = getBookingPredicateByStatus(status, booking -> booking.getItem().getOwner().getId() == userId);

        List<Booking> bookingList = bookingRepository.findAll();
        return bookingList.stream()
                .filter(filter)
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

    }

    private void checkBookingDates(BookingDto bookingDto) throws ValidationException {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Start or End is null");
        }
        LocalDateTime now = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(now) || bookingDto.getEnd().isBefore(now)
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Start or End is wrong");
        }
    }
}
