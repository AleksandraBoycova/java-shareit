package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId) throws Exception;

    ItemDto update(long itemId, ItemDto itemDto, long userId) throws Exception;

    ItemDto delete(long id, long userId) throws Exception;

    ItemDto getById(long id, long userId) throws Exception;

    List<ItemDto> getAll(long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto) throws Exception;
}
