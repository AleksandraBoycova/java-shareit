package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.BaseTest;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingState.APPROVED;
import static ru.practicum.shareit.booking.BookingState.WAITING;

@AutoConfigureMockMvc
@WebMvcTest({BookingServiceImpl.class, BookingRepository.class, UserRepository.class, ItemRepository.class})
class BookingServiceImplTest extends BaseTest {

    @Autowired
    private BookingServiceImpl service;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    BookingRepository bookingRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    void create() throws Exception {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(7L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(buildBooking(5L, item, booker, NOW.plusDays(3), NOW.plusDays(4), WAITING));

        BookingDto bookingDto = service.create(buildBookingDto(null, 2L, null, NOW.plusDays(3), NOW.plusDays(4), null), 7L);
        assertEquals(5L, bookingDto.getId());
        assertEquals(NOW.plusDays(3), bookingDto.getStart());
        assertEquals(NOW.plusDays(4), bookingDto.getEnd());
        assertEquals(WAITING, bookingDto.getStatus());
        assertEquals(7L, bookingDto.getBookerId());
        assertEquals(2L, bookingDto.getItemId());
    }

    @ParameterizedTest
    @MethodSource("prepareDataForCreateWithValidationError")
    void createValidationError(BookingDto bookingDto, String expectedErrorMessage) {
        assertThrows(ValidationException.class, () -> service.create(bookingDto, 5L), expectedErrorMessage);
    }

    @Test
    void createUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.create(buildBookingDto(null, 5L, null,
                        NOW.plusDays(1), NOW.plusDays(2), null), 5L), "User not found");
    }

    @Test
    void createItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(4L, "mail@mail.com", "user")));
        assertThrows(ItemNotFoundException.class,
                () -> service.create(buildBookingDto(null,
                        5L, null, NOW.plusDays(1),
                        NOW.plusDays(2), null), 5L), "Item not found");
    }

    @Test
    void createBookerEqualsOwnerException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(buildItem(2L, "item", "description", true, owner, null)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        assertThrows(ItemNotFoundException.class,
                () -> service.create(buildBookingDto(null,
                        2L, null, NOW.plusDays(1),
                        NOW.plusDays(2), null), 4L), "Item not found");
    }

    @Test
    void createItemNotAvailableException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(7L, "mail@mail.com", "user");
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(buildItem(2L, "item", "description", false, owner, null)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(ItemNotAvailableException.class, () -> service.create(buildBookingDto(null, 2L, null, NOW.plusDays(1), NOW.plusDays(2), null), 7L), "Item not available");

    }

    @Test
    void updateUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.update(1L, buildBookingDto(null, 5L, null,
                        NOW.plusDays(1), NOW.plusDays(2), null), 5L, null), "User not found");
    }

    @Test
    void updateItemNotFoundException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, WAITING)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.update(1L, buildBookingDto(null,
                        5L, null, NOW.plusDays(1),
                        NOW.plusDays(2), null), 5L, null), "Item not found");
    }

    @Test
    void updateBookingNotFoundException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class,
                () -> service.update(1L, buildBookingDto(null,
                        2L, null, NOW.plusDays(1),
                        NOW.plusDays(2), null), 4L, null), "Booking not found");
    }

    @Test
    void updateValidationException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        Booking booking = buildBooking(5L, item, owner, NOW, NOW, WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class,
                () -> service.update(1L, new BookingDto(), 3L, null), "Error");
    }

    @Test
    void updateBookerEqualsOwner() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(3L, "mail3@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, WAITING)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BookingNotFoundException.class,
                () -> service.update(1L, new BookingDto(), 3L, true), "Booking Not Found");
    }

    @Test
    void updateApproveForApproved() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(3L, "mail3@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, APPROVED)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BookingNotFoundException.class,
                () -> service.update(1L, new BookingDto(), 3L, true), "Booking Not Found");
    }

    @Test
    void updateUnauthorized() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(3L, "mail3@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, WAITING)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(UnauthorizedException.class,
                () -> service.update(1L, buildBookingDto(null,
                        2L, null, NOW.plusDays(1),
                        NOW.plusDays(2), null), 3L, null), "Unauthorized");
    }

    @Test
    void updateApprovedNull() throws Exception {
        User owner = buildUser(4L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, WAITING)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(buildBooking(5L, item, owner, NOW.plusDays(3), NOW.plusDays(4), WAITING));

        BookingDto bookingDto = service.update(1L, buildBookingDto(null,
                2L, null, NOW.plusDays(1),
                NOW.plusDays(2), null), 3L, null);
        assertEquals(5L, bookingDto.getId());
        assertEquals(NOW.plusDays(3), bookingDto.getStart());
        assertEquals(NOW.plusDays(4), bookingDto.getEnd());
        assertEquals(WAITING, bookingDto.getStatus());
        assertEquals(4L, bookingDto.getBookerId());
        assertEquals(2L, bookingDto.getItemId());

    }

    @Test
    void update() throws Exception {
        User owner = buildUser(4L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(buildBooking(5L, item, owner, NOW, NOW, WAITING)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(buildBooking(5L, item, owner, NOW.plusDays(3), NOW.plusDays(4), WAITING));

        BookingDto bookingDto = service.update(1L, buildBookingDto(null,
                2L, null, NOW.plusDays(1),
                NOW.plusDays(2), null), 3L, true);
        assertEquals(5L, bookingDto.getId());
        assertEquals(NOW.plusDays(3), bookingDto.getStart());
        assertEquals(NOW.plusDays(4), bookingDto.getEnd());
        assertEquals(WAITING, bookingDto.getStatus());
        assertEquals(4L, bookingDto.getBookerId());
        assertEquals(2L, bookingDto.getItemId());

    }

    @Test
    void getByIdUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getById(1L, 3L), "User not found");
    }

    @Test
    void getByIdBookingNotFoundException() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class,
                () -> service.getById(1L, 3L), "Booking not found");
    }

    @Test
    void getByIdOwnerNotEqualsBooker() {
        User owner = buildUser(4L, "mail@mail.com", "user");
        User booker = buildUser(3L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        Booking booking = buildBooking(5L, item, owner, NOW.plusDays(3), NOW.plusDays(4), WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(BookingNotFoundException.class,
                () -> service.getById(1L, 3L), "Not found for booker");
    }

    @Test
    void getById() throws Exception {
        User owner = buildUser(4L, "mail@mail.com", "user");
        Item item = buildItem(2L, "item", "description", true, owner, null);
        Booking booking = buildBooking(5L, item, owner, NOW.plusDays(3), NOW.plusDays(4), WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any())).thenReturn(buildBooking(5L, item, owner, NOW.plusDays(3), NOW.plusDays(4), WAITING));

        BookingDto bookingDto = service.getById(3L, 7L);
        assertEquals(5L, bookingDto.getId());
        assertEquals(NOW.plusDays(3), bookingDto.getStart());
        assertEquals(NOW.plusDays(4), bookingDto.getEnd());
        assertEquals(WAITING, bookingDto.getStatus());
        assertEquals(4L, bookingDto.getBookerId());
        assertEquals(2L, bookingDto.getItemId());
    }

    @Test
    void getAllUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getAll(1L, "ALL", 0, 20), "User not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"CURRENT", "PAST", "WAITING", "REJECTED", "FUTURE", "ALL"})
    void getAll(String status) throws Exception {
        List<Booking> bookings = List.of(buildBooking(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBooking(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBooking(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingDtos = service.getAll(1L, status, 0, 20);
        switch (status) {
            case "CURRENT":
                verify(bookingRepository, times(1)).findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerId(anyLong(), any());
                break;
            case "PAST":
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(1)).findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerId(anyLong(), any());
                break;
            case "WAITING":
            case "REJECTED":
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerId(anyLong(), any());
                break;
            case "FUTURE":
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(1)).findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerId(anyLong(), any());
                break;
            case "ALL":
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByBookerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
                break;
        }
        assertEquals(3L, bookingDtos.get(0).getId());
        assertEquals(1L, bookingDtos.get(1).getId());
        assertEquals(2L, bookingDtos.get(2).getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CURRENT", "PAST", "WAITING", "REJECTED", "FUTURE", "ALL"})
    void getItemsForUser(String status) throws Exception {
        List<Booking> bookings = List.of(buildBooking(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBooking(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBooking(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any())).thenReturn(new PageImpl<>(bookings));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(bookings));
        when(itemRepository.findAll()).thenReturn(List.of(buildItem(2L, "item", "description", true,
                buildUser(1L, "mail@mail.com", "user"), null)));

        List<BookingDto> bookingDtos = service.getItemsForUser(1L, status, 0, 20);
        switch (status) {
            case "CURRENT":
                verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerId(anyLong(), any());
                break;
            case "PAST":
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerId(anyLong(), any());
                break;
            case "WAITING":
            case "REJECTED":
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerId(anyLong(), any());
                break;
            case "FUTURE":
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerId(anyLong(), any());
                break;
            case "ALL":
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfter(anyLong(), anyList(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndEndBeforeAndStatus(anyLong(), any(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
                verify(bookingRepository, times(0)).findAllByItemOwnerIdAndStatusInAndStartAfter(anyLong(), anyList(), any(), any());
                verify(bookingRepository, times(1)).findAllByItemOwnerId(anyLong(), any());
                break;
        }
        assertEquals(3L, bookingDtos.get(0).getId());
        assertEquals(1L, bookingDtos.get(1).getId());
        assertEquals(2L, bookingDtos.get(2).getId());
    }

    @Test
    void getAllUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        assertThrows(ValidationException.class, () -> service.getAll(8L, "DONE", 0, 20), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getItemsForUserUnauthorizedException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findAll()).thenReturn(List.of(buildItem(2L, "item", "description", true,
                buildUser(1L, "mail@mail.com", "user"), null)));
        assertThrows(UnauthorizedException.class, () -> service.getItemsForUser(8L, "ALL", 0, 20));
    }

    @Test
    void getItemsForUserUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getItemsForUser(8L, "ALL", 0, 20), "User not found");
    }

    @Test
    void getItemsForUserUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findAll()).thenReturn(List.of(buildItem(2L, "item", "description", true,
                buildUser(1L, "mail@mail.com", "user"), null)));
        assertThrows(ValidationException.class, () -> service.getItemsForUser(1L, "DONE", 0, 20), "Unknown state: UNSUPPORTED_STATUS");
    }


    private static Stream<Arguments> prepareDataForCreateWithValidationError() {
        return Stream.of(
                Arguments.of(buildBookingDto(null, 5L, 3L, null, NOW.plusDays(2), null), "Start or End is null"),
                Arguments.of(buildBookingDto(null, 5L, 3L, NOW.plusDays(5), null, null), "Start or End is null"),
                Arguments.of(buildBookingDto(null, 5L, 3L, null, null, null), "Start or End is null"),
                Arguments.of(buildBookingDto(null, 5L, 3L, NOW.plusDays(2), NOW.plusDays(2), null), "Start or End is wrong"),
                Arguments.of(buildBookingDto(null, 5L, 3L, NOW.minusDays(4), NOW.plusDays(3), null), "Start or End is wrong"),
                Arguments.of(buildBookingDto(null, 5L, 3L, NOW.plusDays(4), NOW.minusDays(3), null), "Start or End is wrong"),
                Arguments.of(buildBookingDto(null, 5L, 3L, NOW.plusDays(10), NOW.plusDays(4), null), "Start or End is wrong")
        );
    }
}