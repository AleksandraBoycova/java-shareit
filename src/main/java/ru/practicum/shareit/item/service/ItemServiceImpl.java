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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) throws UserNotFoundException, ValidationException {
        User owner = userRepository.getById(userId);
        owner.setId(userId);
        validate(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item i = itemRepository.save(item);
        return ItemMapper.toItemDto(i);
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) throws UserNotFoundException, ItemNotFoundException, UnauthorizedException {

        User user = userRepository.getById(userId);
        Item itemToUpdate = itemRepository.getById(itemId);
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
        Item item = itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto delete(long id, long userId) throws ItemNotFoundException, UserNotFoundException, UnauthorizedException {
        User user = userRepository.getById(userId);
        Item itemToDelete = itemRepository.getById(id);
        if (!Objects.equals(itemToDelete.getOwner().getId(), userId)) {
            throw new UnauthorizedException("User can not delete this item!");
        }
         itemRepository.deleteById(id);
        return ItemMapper.toItemDto(itemToDelete);
    }

    @Override
    public ItemDto getById(long id) throws ItemNotFoundException {
        Item item = itemRepository.getById(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return itemRepository.findAll().stream().filter(item -> Objects.equals(item.getOwner()
                .getId(), userId)).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validate(ItemDto itemDto) throws ValidationException {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Not valid");
        }
    }
}
