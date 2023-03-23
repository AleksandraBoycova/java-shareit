package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto) throws Exception;

    UserDto update(long userId, UserDto userDto) throws Exception;

    UserDto delete(long id) throws Exception;

    UserDto getById(long id) throws Exception;

    List<UserDto> getAll();
}
