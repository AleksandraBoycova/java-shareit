package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
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
    public ItemDto create(ItemDto itemDto, Long userId) throws UserNotFoundException, ValidationException {
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
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException {
        if (userId == null) {
            throw new ValidationException("User id is null!");
        }
        if (!STORAGE.containsKey(itemId)) {
            throw new ItemNotFoundException("Item not found!");
        }
        UserDto user = userService.getById(userId);
        Item itemToUpdate = STORAGE.get(itemId);
        if (!Objects.equals(itemToUpdate.getOwner().getId(), userId)) {
            throw new UnauthorizedException("User can not update this item!");
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
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto delete(Long id, Long userId) throws ItemNotFoundException, ValidationException, UserNotFoundException, UnauthorizedException {
        if (userId == null) {
            throw new ValidationException("User id is null");
        }
        if (!STORAGE.containsKey(id)) {
            throw new ItemNotFoundException("Item not found!");
        }
        UserDto user = userService.getById(userId);
        Item itemToDelete = STORAGE.get(id);
        if (!Objects.equals(itemToDelete.getOwner().getId(), userId)) {
            throw new UnauthorizedException("User can not delete this item!");
        }
        STORAGE.remove(id);
        COUNTER--;
        return ItemMapper.toItemDto(itemToDelete);
    }

    @Override
    public ItemDto getById(Long id) throws ItemNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new ItemNotFoundException("Item not found!");
        }
        Item item = STORAGE.get(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return getAll().stream().filter(item -> Objects.equals(item.getOwner()
                .getId(), userId)).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return getAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private List<Item> getAll() {
        return new ArrayList<>(STORAGE.values());
    }

    private void validate(ItemDto itemDto) throws ValidationException {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Not valid");
        }
    }
}
