package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId) throws UserNotFoundException, ValidationException;

    ItemDto update(long itemId, ItemDto itemDto, long userId) throws UserNotFoundException, ItemNotFoundException, UnauthorizedException;

    ItemDto delete(long id, long userId) throws ItemNotFoundException, UserNotFoundException, UnauthorizedException;

    ItemDto getById(long id) throws ItemNotFoundException;

    List<ItemDto> getAll(long userId);

    List<ItemDto> search(String text);

    ItemDto addComment (long userId, long itemId, CommentDto commentDto) throws UserNotFoundException, ItemNotFoundException, UnauthorizedException;
}
