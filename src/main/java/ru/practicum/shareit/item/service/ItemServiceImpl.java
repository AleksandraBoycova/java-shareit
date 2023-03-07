package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static Map<Long, Item> STORAGE = new HashMap<>();
    private static long COUNTER = 1;
    private UserService userService;

   @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
       if (userId == null) {
           throw new RuntimeException();
       }
       User owner = UserMapper.toUser(userService.getById(userId));
       owner.setId(userId);
        validate(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(COUNTER++);
        item.setOwner(owner);
        STORAGE.put(item.getId(), item);
        return itemDto;
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new RuntimeException();
        }
       if (!STORAGE.containsKey(itemId)) {
           throw new RuntimeException();
       }
        Item itemToUpdate = STORAGE.get(itemId);
       if (itemToUpdate.getOwner().getId() != userId) {
           throw new RuntimeException();
       }
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
        if (userId == null) {
            throw new RuntimeException();
        }
        if (!STORAGE.containsKey(id)) {
            throw new RuntimeException();
        }
        Item itemToDelete = STORAGE.get(id);
        if (itemToDelete.getOwner().getId() != userId) {
            throw new RuntimeException();
        }
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
