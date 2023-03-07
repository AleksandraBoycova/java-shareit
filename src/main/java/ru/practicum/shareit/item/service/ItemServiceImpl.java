package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static Map<Long, Item> STORAGE = new HashMap<>();
    private static long COUNTER = 1;
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        validate(itemDto);
        return null;
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
       if (!STORAGE.containsKey(itemId)) {
           throw new RuntimeException();
       }
        Item itemToUpdate = STORAGE.get(itemId);
        if (itemDto.getName() != null) {
           itemToUpdate.setName(itemDto.getName());
       }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        return itemDto;
    }

    @Override
    public ItemDto delete(Long id, Long userId) {
        if (!STORAGE.containsKey(id)) {
            throw new RuntimeException();
        }
        Item itemToDelete = STORAGE.get(id);
        STORAGE.remove(id);

        return ItemMapper.toItemDto(itemToDelete);
    }

    @Override
    public ItemDto getById(Long id) {
        if (!STORAGE.containsKey(id)) {
            throw new RuntimeException();
        }
        Item item = STORAGE.get(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return getAll().stream().filter(item -> item.getOwner()
                .getId() == userId).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private List<Item> getAll() {
      return new ArrayList<>(STORAGE.values());
    }

    private void validate (ItemDto itemDto) {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new RuntimeException();
        }
    }
}
