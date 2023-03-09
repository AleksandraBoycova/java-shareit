package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValueException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static Map<Long, User> STORAGE = new HashMap<>();
    private static long COUNTER = 1;


    @Override
    public UserDto create(UserDto userDto) throws ValidationException, DuplicateValueException {
        validateUser(userDto);
        User user = new User();
        user.setId(COUNTER++);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        STORAGE.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) throws UserNotFoundException, DuplicateValueException {
        if (!STORAGE.containsKey(userId)) {
            throw new UserNotFoundException("User not found");
        }
        User userToUpdate = STORAGE.get(userId);
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            boolean emailExists = STORAGE.values().stream().filter(user -> !Objects.equals(user.getId(), userId)).map(User::getEmail).anyMatch(email -> email.equals(userDto.getEmail()));

            if (emailExists) {
                throw new DuplicateValueException("Email exists!");
            }
            userToUpdate.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public UserDto delete(Long id) throws UserNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new UserNotFoundException("User not found");
        }
        User userToDelete = STORAGE.get(id);
        STORAGE.remove(id);
//        COUNTER--;
        return UserMapper.toUserDto(userToDelete);
    }

    @Override
    public UserDto getById(Long id) throws UserNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new UserNotFoundException("User not found");
        }
        User user = STORAGE.get(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return STORAGE.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private void validateUser(UserDto userDto) throws ValidationException, DuplicateValueException {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email is null!");
        }
        boolean containsEmail = STORAGE.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
        if (containsEmail) {
            throw new DuplicateValueException("Email exists");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Name is null or empty");
        }
    }
}
