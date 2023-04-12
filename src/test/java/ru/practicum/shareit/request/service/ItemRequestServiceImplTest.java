package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({ItemRequestServiceImpl.class, ItemRequestRepository.class, UserRepository.class, ItemRepository.class})
class ItemRequestServiceImplTest extends BaseTest {

    @Autowired
    private ItemRequestServiceImpl service;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;

    @Test
    void createUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.create(buildItemRequestDto(1L, 2L, "123"), 2L), "User not found");
    }

    @ParameterizedTest
    @MethodSource("prepareDataForCreate")
    void createValidationError(String description, String expectedMessage) {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        assertThrows(ValidationException.class, () -> service.create(buildItemRequestDto(1L, 2L, description), 2L));
    }

    @Test
    void create() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        when(itemRequestRepository.save(any())).thenReturn(buildItemRequest(1L, buildUser(2L, "email@mail.com", "name"), "abcde"));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(buildItem(1L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), null)));
        ItemRequestDto created = service.create(buildItemRequestDto(1L, 2L, "abcde"), 2L);
        assertEquals(1L, created.getId());
        assertFalse(created.getItems().isEmpty());
    }

    @Test
    void getByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getById(3L, 2L), "User not found");
    }

    @Test
    void getByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> service.getById(2L, 1L), "Item request not found");
    }

    @Test
    void getById() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(buildItemRequest(1L, buildUser(2L, "email@mail.com", "name"), "abcde")));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(buildItem(1L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), null)));
        ItemRequestDto ir = service.getById(2L, 1L);
        assertEquals(1L, ir.getId());
        assertFalse(ir.getItems().isEmpty());
    }

    @Test
    void getAllOwnRequestsUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getAllOwnRequests(3L), "User not found");
    }

    @Test
    void getAllOwnRequests() throws UserNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        ItemRequest e1 = buildItemRequest(1L, buildUser(2L, "email@mail.com", "name"), "abcde");
        ItemRequest e2 = buildItemRequest(2L, buildUser(2L, "email@mail.com", "name"), "abcde");
        ItemRequest e3 = buildItemRequest(3L, buildUser(2L, "email@mail.com", "name"), "abcde");
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(any())).thenReturn(List.of(e1, e2, e3));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(
                buildItem(4L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), e1),
                buildItem(5L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), e1),
                buildItem(6L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), e2),
                buildItem(7L, "name", "description", true, buildUser(3L, "mail@mail.com", "name"), e3)
        ));
        List<ItemRequestDto> requests = service.getAllOwnRequests(2L);
        assertEquals(3, requests.size());
        assertEquals(2, requests.get(0).getItems().size());
        assertEquals(1, requests.get(1).getItems().size());
        assertEquals(1, requests.get(2).getItems().size());
    }

    @Test
    void getAllUserRequestsUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getAllUserRequests(3L, 0, 5), "User not found");
    }

    @Test
    void getAllUserRequests() throws UserNotFoundException, ValidationException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        ItemRequest e1 = buildItemRequest(1L, buildUser(3L, "email@mail.com", "name"), "abcde");
        ItemRequest e2 = buildItemRequest(2L, buildUser(4L, "email@mail.com", "name"), "abcde");
        ItemRequest e3 = buildItemRequest(3L, buildUser(5L, "email@mail.com", "name"), "abcde");
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any())).thenReturn(new PageImpl<>(List.of(e1, e2, e3)));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(
                buildItem(4L, "name", "description", true, buildUser(6L, "mail@mail.com", "name"), e1),
                buildItem(5L, "name", "description", true, buildUser(7L, "mail@mail.com", "name"), e1),
                buildItem(6L, "name", "description", true, buildUser(8L, "mail@mail.com", "name"), e2),
                buildItem(7L, "name", "description", true, buildUser(9L, "mail@mail.com", "name"), e3)
        ));
        List<ItemRequestDto> requests = service.getAllUserRequests(2L, 0, 15);
        assertEquals(3, requests.size());
        assertEquals(2, requests.get(0).getItems().size());
        assertEquals(1, requests.get(1).getItems().size());
        assertEquals(1, requests.get(2).getItems().size());
    }

    @Test
    void getAllUserRequestsEmpty() throws UserNotFoundException, ValidationException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        List<ItemRequestDto> requests = service.getAllUserRequests(2L, null, null);
        assertTrue(requests.isEmpty());
    }

    @Test
    void getAllUserRequestsValidationError() throws UserNotFoundException, ValidationException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(2L, "mail@mail.com", "name")));
        assertThrows(ValidationException.class, () -> service.getAllUserRequests(2L, 0, 0), "Error");
    }

    private static Stream<Arguments> prepareDataForCreate() {
        return Stream.of(
                Arguments.of(null, "Description is null"),
                Arguments.of("    ", "Description is null"),
                Arguments.of("", "Description is null")
        );
    }

}