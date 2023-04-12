package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({UserServiceImpl.class, UserRepository.class})
class UserServiceImplTest extends BaseTest {
    @Autowired
    private UserServiceImpl service;
    @MockBean
    UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("prepareDataForCreate")
    void create(String email, String name, String expectedMessage) {
        UserDto userDto = buildUserDto(1L, email, name);
        assertThrows(ValidationException.class, () -> service.create(userDto), expectedMessage);
    }

    @Test
    void create() throws Exception {
        when(userRepository.save(any())).thenReturn(buildUser(2L, "email@user.com", "name"));
        UserDto createdUser = service.create(buildUserDto(0L, "email@user.com", "name"));
        assertEquals(2L, createdUser.getId());
        assertEquals("email@user.com", createdUser.getEmail());
        assertEquals("name", createdUser.getName());
    }

    @Test
    void updateUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.update(2L, buildUserDto(2L, "email@mail.com", "name")), "User not found");
    }

    @Test
    void update() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(3L, "email@mail.com", "name")));
        when(userRepository.save(any())).thenReturn(buildUser(3L, "updated@mail.com", "name updated"));
        UserDto updated = service.update(3L, buildUserDto(3L, "update@mail.com", "name"));
        assertEquals(3L, updated.getId());
        assertEquals("updated@mail.com", updated.getEmail());
        assertEquals("name updated", updated.getName());
    }

    @Test
    void deleteWithError() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.delete(2L), "User not found");
    }

    @Test
    void delete() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(3L, "email@mail.com", "name")));
        doNothing().when(userRepository).deleteById(anyLong());
        UserDto updated = service.delete(3L);
        assertEquals(3L, updated.getId());
        assertEquals("email@mail.com", updated.getEmail());
        assertEquals("name", updated.getName());
    }

    @Test
    void getByIdWithError() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.getById(2L), "User not found");
    }

    @Test
    void getById() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(buildUser(3L, "email@mail.com", "name")));
        UserDto updated = service.getById(3L);
        assertEquals(3L, updated.getId());
        assertEquals("email@mail.com", updated.getEmail());
        assertEquals("name", updated.getName());
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(List.of(
                buildUser(1L, "email@mail.com", "name"),
                buildUser(2L, "user@mail.com", "anna")
        ));
        List<UserDto> users = service.getAll();
        assertEquals(2, users.size());
    }

    private static Stream<Arguments> prepareDataForCreate() {
        return Stream.of(
                Arguments.of(null, "name", "Email is null!"),
                Arguments.of("emil@mail.com", "   ", "Name is null or empty"),
                Arguments.of("emil@mail.com", "", "Name is null or empty"),
                Arguments.of("emil@mail.com", null, "Name is null or empty")
        );
    }

}