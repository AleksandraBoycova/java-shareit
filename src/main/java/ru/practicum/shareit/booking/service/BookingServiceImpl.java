package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
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
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
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
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
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
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (booking.getItem().getOwner().equals(booker) || booking.getBooker().equals(booker)) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new BookingNotFoundException("Not found for booker");
    }

    @Override
    public List<BookingDto> getAll(long userId, String status, Integer from, Integer size) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Booking> bookings = getBookingsByStatusForBooker(status, userId, PageRequest.of(from / size, size, Sort.by("start").descending()));

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByStatusForBooker(String status, Long userId, PageRequest pageRequest) throws ValidationException {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(userId, List.of(BookingState.APPROVED, BookingState.WAITING, BookingState.REJECTED), now, now, pageRequest).getContent();
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(userId, now, BookingState.APPROVED, pageRequest).getContent();
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingState.WAITING, pageRequest).getContent();
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingState.REJECTED, pageRequest).getContent();
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStatusInAndStartAfter(userId, List.of(BookingState.APPROVED, BookingState.WAITING), now, pageRequest).getContent();
            case "ALL":
                return bookingRepository.findAllByBookerId(userId, pageRequest).getContent();
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> getBookingsByStatusForOwner(String status, Long userId, PageRequest pageRequest) throws ValidationException {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(userId, List.of(BookingState.APPROVED, BookingState.WAITING, BookingState.REJECTED), now, now, pageRequest).getContent();
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeAndStatus(userId, now, BookingState.APPROVED, pageRequest).getContent();
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.WAITING, pageRequest).getContent();
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.REJECTED, pageRequest).getContent();
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStatusInAndStartAfter(userId, List.of(BookingState.APPROVED, BookingState.WAITING), now, pageRequest).getContent();
            case "ALL":
                return bookingRepository.findAllByItemOwnerId(userId, pageRequest).getContent();
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getItemsForUser(long userId, String status, Integer from, Integer size) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (itemRepository.findAll().stream().noneMatch(item -> item.getOwner().getId().equals(userId))) {
            throw new UnauthorizedException("Unauthorized");
        }
        List<Booking> bookings = getBookingsByStatusForOwner(status, userId, PageRequest.of(from / size, size, Sort.by("start").descending()));

        return bookings.stream()
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
