package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @ParameterizedTest
    @MethodSource("prepareDataForCreate")
    void create(ItemDto itemDto) {
        assertThrows(ValidationException.class, () -> service.create(itemDto, 5L), "Not valid");
    }

    @Test
    void createUserNotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.create(buildItemDto(1L, "name", "abcde", true), 2L));
    }

    @Test
    void create() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        User user = buildUser(3L, "email@mail.com", "name");
        ItemRequest itemRequest = buildItemRequest(4L, user, "bavscd");
        when(itemRequestRepository.findById(anyLong())).thenReturn(
                Optional.of(itemRequest));
        when(repository.save(any())).thenReturn(buildItem(2L, "name", "asdfg", true, user, itemRequest));
        ItemDto it = service.create(buildItemDto(2L, "name", "asdfgrt", true), 3L);
        assertEquals(2L, it.getId());
    }

    @Test
    void updateUserNotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.update(3L, buildItemDto(1L, "name", "abcde", true), 2L));
    }

    @Test
    void updateItemNotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.update(3L, buildItemDto(1L, "name", "abcde", true), 2L));
    }

    @Test
    void updateUnauthorized(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        assertThrows(UnauthorizedException.class, () -> service.update(3L, buildItemDto(1L, "name", "abcde", true), 6L));
    }

    @Test
    void update() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        when(repository.save(any())).thenReturn(buildItem(2L, "name", "asdfg", true, user, null));
        ItemDto updated = service.update(3L, buildItemDto(1L, "name", "abcde", true), 2L);
        assertEquals(2L, updated.getId());
    }

    @Test
    void delete() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        doNothing().when(repository).deleteById(anyLong());
        ItemDto deleted = service.delete(3L,  2L);
        assertEquals(2L, deleted.getId());
    }

    @Test
    void deleteUnauthorized() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        assertThrows(UnauthorizedException.class, () -> service.delete(3L,  6L));
    }

    @Test
    void deleteUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.delete(3L, 2L));
    }

    @Test
    void deleteItemNotfound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.delete(3L,  2L));
    }

    @Test
    void getByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        assertThrows(UserNotFoundException.class, () -> service.getById(3L, 2L));
    }

    @Test
    void getByIdItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.getById(3L,  2L));
    }

    @Test
    void getByIdByOwner() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      user, null)));
        ItemDto itemDto = service.getById(3L,  2L);
        assertEquals(2L, itemDto.getId());
        assertNull( itemDto.getNextBooking());
        assertNull( itemDto.getLastBooking());
    }

    @Test
    void getById() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = buildItem(2L, "item", "description", true, user, null);
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(item));
        when(bookingRepository.findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc(anyList(), any(), any())).thenReturn(List.of(
                buildBooking(2L, buildItem(1L, "abc", "def", true, user, null),
                             user, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED)));
        when(bookingRepository.findAllByItemIdInAndStatusAndStartAfterOrderByStart(anyList(), any(), any())).thenReturn(List.of(
                buildBooking(8L, buildItem(7L, "abc", "def", true, user, null),
                             user, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED)
                                                                                                                               ));
        ItemDto itemDto = service.getById(3L, 2L);
        assertNotNull(itemDto.getNextBooking());
        assertNotNull(itemDto.getLastBooking());
        assertEquals(8L, itemDto.getNextBooking().getId());
        assertEquals(2L, itemDto.getLastBooking().getId());
    }

    @Test
    void getByIdEmptyLastNext() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = buildItem(2L, "item", "description", true, user, null);
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(item));
        when(bookingRepository.findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc(anyList(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemIdInAndStatusAndStartAfterOrderByStart(anyList(), any(), any())).thenReturn(new ArrayList<>());
        ItemDto itemDto = service.getById(3L, 2L);
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getLastBooking());
    }

    @Test
    void getAll() {
        User user1 = buildUser(2L, "email", "name");
        User user2 = buildUser(3L, "email", "name");
        User user3 = buildUser(4L, "email", "name");
        Item e1 = buildItem(2L, "item", "description", true, user1, null);
        Item e2 = buildItem(3L, "item", "description", true, user1, null);
        Item e3 = buildItem(4L, "item", "description", true, user1, null);
        Item e4 = buildItem(5L, "item", "description", true, user1, null);
        Item e5 = buildItem(6L, "item", "description", true, user1, null);
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
        Item e1 = buildItem(2L, "item", "description", true, user1, null);
        Item e2 = buildItem(3L, "item", "description", true, user1, null);

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

    @Test
    void search() {
        User user = buildUser(2L, "email", "name");
        Item e1 = buildItem(2L, "item", "description", true, user, null);
        Item e5 = buildItem(6L, "item", "description", true, user, null);
        when(repository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any())).thenReturn(new PageImpl<>(List.of(e1, e5)));
        List<ItemDto> items = service.search("description", 0, 15);
        assertEquals(2, items.size());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"  ", ""})
    void searchEmpty(String text) {
        List<ItemDto> itemDtos = service.search(text, 0, 15);
        assertTrue(itemDtos.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"  ", ""})
    void addCommentValidationError(String text) {
        assertThrows(ValidationException.class, () -> service.addComment(2L, 4L, buildCommentDto(1L, text, LocalDateTime.now(), "name")), "Empty comment");
    }

    @Test
    void addCommentUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(buildItem(2L, "item", "description", true,
                                      buildUser(2L, "email@mail.com", "name"), null)));
        assertThrows(UserNotFoundException.class, () -> service.addComment(3L, 2L, buildCommentDto(1L, "abc", LocalDateTime.now(), "name")));
    }

    @Test
    void addCommentItemNotFund() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "email@mail.com", "name")));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.addComment(3L,  2L, buildCommentDto(1L, "abc", LocalDateTime.now(), "name")));
    }

    @Test
    void addComment() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = buildItem(2L, "item", "description", true, user, null);
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(item));
        when(bookingRepository.findAllByBookerId(anyLong())).thenReturn(List.of(
                buildBooking(2L, item, user, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(5), BookingState.APPROVED)
                                                                                              ));
        when(commentRepository.save(any())).thenReturn(buildComment(2L, "text", LocalDateTime.now(), "name", user));
        CommentDto commentDto = service.addComment(2L, 2L, buildCommentDto(1L, "text", LocalDateTime.now(), "name"));
        assertEquals(2L, commentDto.getId());
    }

    @Test
    void addCommentUnauthorized() throws Exception {
        User user = buildUser(2L, "email@mail.com", "name");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = buildItem(2L, "item", "description", true, user, null);
        when(repository.findById(anyLong())).thenReturn(
                Optional.of(item));
        when(bookingRepository.findAllByBookerId(anyLong())).thenReturn(List.of(
                buildBooking(2L, item, user, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(5), BookingState.WAITING)
                                                                               ));
        assertThrows(ValidationException.class, () -> service.addComment(2L, 2L, buildCommentDto(1L, "text", LocalDateTime.now(), "nmae")));
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