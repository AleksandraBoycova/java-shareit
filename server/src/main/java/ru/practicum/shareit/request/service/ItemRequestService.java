package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto userDto, Long userId) throws Exception;

    ItemRequestDto getById(Long userId, Long id) throws Exception;

    List<ItemRequestDto> getAllOwnRequests(Long userId) throws UserNotFoundException;

    List<ItemRequestDto> getAllUserRequests(Long userId, Integer from, Integer size) throws UserNotFoundException, ValidationException;
}
