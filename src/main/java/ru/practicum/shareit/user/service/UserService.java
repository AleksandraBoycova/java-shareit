package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DuplicateValueException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto) throws ValidationException, DuplicateValueException;

    UserDto update(Long userId, UserDto userDto) throws UserNotFoundException, ValidationException, DuplicateValueException;

    UserDto delete(Long id) throws UserNotFoundException;

    UserDto getById(Long id) throws UserNotFoundException;

    List<UserDto> getAll();
}
