package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId) throws UserNotFoundException, ValidationException;

    ItemDto update(Long itemId, ItemDto itemDto, Long userId) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException;

    ItemDto delete(Long id, Long userId) throws ItemNotFoundException, ValidationException, UserNotFoundException, UnauthorizedException;

    ItemDto getById(Long id) throws ItemNotFoundException;

    List<ItemDto> getAll(Long userId);
    List<ItemDto> search(String text);
}
