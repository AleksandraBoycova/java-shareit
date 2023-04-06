package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({ItemServiceImpl.class, ItemRepository.class, BookingRepository.class, UserRepository.class, CommentRepository.class, ItemRequestRepository.class})
class ItemServiceImplTest extends BaseTest {

    @Autowired
    private ItemServiceImpl service;
    @MockBean
    ItemRepository repository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    private BookingRepository     bookingRepository;
    @MockBean
    private CommentRepository     commentRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @ParameterizedTest
    @MethodSource("prepareDataForCreate")
    void create(ItemDto itemDto) {
        assertThrows(ValidationException.class, () -> service.create(itemDto, 5L), "Not valid");
    }

    @Test
    void getAll() {
        User user1 = buildUser(2L, "email", "name");
        User user2 = buildUser(3L, "email", "name");
        User user3 = buildUser(4L, "email", "name");
        Item e1    = buildItem(2L, "item", "description", true, user1);
        Item e2    = buildItem(3L, "item", "description", true, user1);
        Item e3    = buildItem(4L, "item", "description", true, user1);
        Item e4    = buildItem(5L, "item", "description", true, user1);
        Item e5    = buildItem(6L, "item", "description", true, user1);
        when(repository.findAllByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(e1, e2, e3, e4, e5)));
        when(bookingRepository.findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter(anyList(), any(), any(), any(), any())).thenReturn(List.of(
                buildBooking(4L, e1, user2, getDateFromString("2023-04-02 12:00:00"), getDateFromString("2023-04-03 12:00:00"), BookingState.APPROVED),
                buildBooking(6L, e2, user2, getDateFromString("2023-04-03 12:00:00"), getDateFromString("2023-04-04 12:00:00"), BookingState.APPROVED),
                buildBooking(7L, e3, user3, getDateFromString("2023-04-05 12:00:00"), getDateFromString("2023-04-12 12:00:00"), BookingState.APPROVED),
                buildBooking(8L, e4, user3, getDateFromString("2023-04-04 12:00:00"), getDateFromString("2023-04-25 12:00:00"), BookingState.APPROVED),
                buildBooking(9L, e5, user3, getDateFromString("2023-04-03 12:00:00"), getDateFromString("2023-04-08 12:00:00"), BookingState.APPROVED)
        ));
        when(bookingRepository.findAllByItemIdInAndStatusAndStartAfter(anyList(), any(), any())).thenReturn(List.of(
                buildBooking(14L, e1, user2, getDateFromString("2023-04-08 12:00:00"), getDateFromString("2023-04-13 12:00:00"), BookingState.APPROVED),
                buildBooking(16L, e2, user2, getDateFromString("2023-04-09 12:00:00"), getDateFromString("2023-04-14 12:00:00"), BookingState.APPROVED),
                buildBooking(17L, e3, user3, getDateFromString("2023-04-10 12:00:00"), getDateFromString("2023-04-12 12:00:00"), BookingState.APPROVED),
                buildBooking(18L, e4, user3, getDateFromString("2023-04-14 12:00:00"), getDateFromString("2023-04-25 12:00:00"), BookingState.APPROVED),
                buildBooking(19L, e5, user3, getDateFromString("2023-04-23 12:00:00"), getDateFromString("2023-04-24 12:00:00"), BookingState.APPROVED)
        ));

        List<ItemDto> items = service.getAll(2L, 0, 10);
        assertEquals(4L, items.get(0).getLastBooking().getId());
        assertEquals(14L, items.get(0).getNextBooking().getId());

        assertEquals(6L, items.get(1).getLastBooking().getId());
        assertEquals(16L, items.get(1).getNextBooking().getId());

        assertEquals(7L, items.get(2).getLastBooking().getId());
        assertEquals(17L, items.get(2).getNextBooking().getId());

        assertEquals(8L, items.get(3).getLastBooking().getId());
        assertEquals(18L, items.get(3).getNextBooking().getId());

        assertEquals(9L, items.get(4).getLastBooking().getId());
        assertEquals(19L, items.get(4).getNextBooking().getId());
    }

    @Test
    void getAllEmptyLastAndNext() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = buildUser(2L, "email", "name");
        User user2 = buildUser(3L, "email", "name");
        Item e1    = buildItem(2L, "item", "description", true, user1);
        Item e2    = buildItem(3L, "item", "description", true, user1);

        when(repository.findAllByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(e1, e2)));
        when(bookingRepository.findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter(anyList(), any(), any(), any(), any())).thenReturn(List.of(
                buildBooking(6L, e2, user2, now.minusDays(3), now.minusDays(2), BookingState.APPROVED)
        ));
        when(bookingRepository.findAllByItemIdInAndStatusAndStartAfter(anyList(), any(), any())).thenReturn(List.of(
                buildBooking(4L, e2, user2, now.plusDays(2), now.plusDays(7), BookingState.APPROVED)
        ));

        List<ItemDto> items = service.getAll(2L, 0, 10);
        assertEquals(6L, items.get(1).getLastBooking().getId());
        assertEquals(4L, items.get(1).getNextBooking().getId());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
    }

    private static Stream<Arguments> prepareDataForCreate() {
        return Stream.of(
                Arguments.of(buildItemDto(1L, null, "description", true), "Not valid"),
                Arguments.of(buildItemDto(1L, "name", null, true), "Not valid"),
                Arguments.of(buildItemDto(1L, "", "description", true), "Not valid"),
                Arguments.of(buildItemDto(1L, " ", "description", true), "Not valid"),
                Arguments.of(buildItemDto(1L, "name", "", true), "Not valid"),
                Arguments.of(buildItemDto(1L, "name", "      ", true), "Not valid")
        );
    }
}