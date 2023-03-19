package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
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
        Item item = itemRepository.findById(bookingDto.getItem()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        booking.setBooker(booker);
        booking.setItem(item);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto update(long bookingId, BookingDto bookingDto, long userId, Boolean approved) throws UserNotFoundException, ItemNotFoundException, BookingNotFoundException, UnauthorizedException {
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDto.getItem()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (approved != null) {
           if (item.getOwner().getId() != booker.getId()) {
               throw new UnauthorizedException("Unauthorized");
           }
            booking.setStatus(approved?BookingState.APPROVED:BookingState.REJECTED);
           return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        if (booking.getBooker().getId() != booker.getId()) {
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
    public BookingDto getById(long id, long userId) throws UserNotFoundException, BookingNotFoundException {
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (booking.getItem().getOwner().equals(booker) || booking.getBooker().equals(booker)) {
            return BookingMapper.toBookingDto(booking);
        }
        return null;
    }

    @Override
    public List<BookingDto> getAll(long userId, String status) throws ValidationException {
        if (status == null) {
            status = "ALL";
        }
        Predicate<Booking> filter = getBookingPredicateByStatus(status, userId);

        return bookingRepository.findAll().stream()
                .filter(filter)
                .map(BookingMapper::toBookingDto)
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .collect(Collectors.toList());
    }

    private static Predicate<Booking> getBookingPredicateByStatus(String status, long userId) throws ValidationException {
        Predicate<Booking> filter = booking -> booking.getBooker().getId() == userId;
        switch (status) {
            case "CURRENT":
                filter = booking -> booking.getStart().isBefore(LocalDate.now())
                        && booking.getEnd().isAfter(LocalDate.now())
                        && booking.getStatus().equals(BookingState.APPROVED);
                break;
            case "PAST":
                filter = booking -> booking.getStatus().equals(BookingState.APPROVED)
                        && booking.getEnd().isBefore(LocalDate.now());
                break;
            case "WAITING":
                filter = booking -> booking.getStatus().equals(BookingState.WAITING);
                break;
            case "REJECTED":
                filter = booking -> booking.getStatus().equals(BookingState.REJECTED);
                break;
            case "ALL":
                break;
            default:
                throw new ValidationException("Unsupported state");
        }
        return filter;
    }

    @Override
    public List<ItemDto> getItemsForUser (long userId, String status) throws ValidationException {
        if (status == null) {
            status = "ALL";
        }
        Predicate<Booking> filter = getBookingPredicateByStatus(status, userId);

        return bookingRepository.findAll().stream()
                .filter(filter)
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .map(Booking::getItem)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

    }
    private void checkBookingDates(BookingDto bookingDto) throws ValidationException {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Start or End is null");
        }
        if (bookingDto.getStart().isBefore(LocalDate.now()) || bookingDto.getEnd().isBefore(LocalDate.now())
                || bookingDto.getStart().equals(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Start or End is wrong");
        }
    }
}
