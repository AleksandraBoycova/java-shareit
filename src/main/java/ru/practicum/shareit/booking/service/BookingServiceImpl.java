package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
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
    public BookingDto update(long bookingId, BookingDto bookingDto, long userId) {
        return null;
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
    public List<BookingDto> getAll(long userId, String status) {
       return bookingRepository.findAll().stream()
               .filter(booking -> booking.getStatus().equals(status))
               .map(BookingMapper::toBookingDto)
               .collect(Collectors.toList());
    }

    private void checkBookingDates (BookingDto bookingDto) throws ValidationException {
        if(bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Start or End is null");
        }
        if (bookingDto.getStart().isBefore(LocalDate.now()) || bookingDto.getEnd().isBefore(LocalDate.now())
        || bookingDto.getStart().equals(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Start or End is wrong");
        }
    }
}
