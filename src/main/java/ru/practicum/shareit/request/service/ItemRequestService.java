package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto userDto, Long userId) throws Exception;

    ItemRequestDto getById(long id, long userId) throws Exception;

    List<ItemRequestDto> getAll(Long userId, Long from, Long size) throws UserNotFoundException;
}
