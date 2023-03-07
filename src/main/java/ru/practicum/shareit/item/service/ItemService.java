package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    ItemDto delete(Long id, Long userId);

    ItemDto getById(Long id);

    List<ItemDto> getAll(Long userId);
}
