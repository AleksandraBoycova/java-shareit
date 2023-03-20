package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
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
    private BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) throws UserNotFoundException, ValidationException {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        owner.setId(userId);
        validate(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item i = itemRepository.save(item);
        return ItemMapper.toItemDto(i);
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) throws UserNotFoundException, ItemNotFoundException, UnauthorizedException {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item itemToDelete = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (!Objects.equals(itemToDelete.getOwner().getId(), userId)) {
            throw new UnauthorizedException("User can not delete this item!");
        }
         itemRepository.deleteById(id);
        return ItemMapper.toItemDto(itemToDelete);
    }

    @Override
    public ItemDto getById(long id) throws ItemNotFoundException {
        Item itemToDelete = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        return ItemMapper.toItemDto(itemToDelete);
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

    @Override
    public ItemDto addComment(long userId, long itemId, CommentDto commentDto) throws UserNotFoundException, ItemNotFoundException, UnauthorizedException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        boolean userBookedItem = bookingRepository.findAllByBookerId(userId).stream().anyMatch(booking -> Objects.equals(booking.getItem().getId(), item.getId()));
        if (!userBookedItem) {
            throw new UnauthorizedException("Unauthorized");
        }
        item.getComments().add(CommentMapper.toComment(commentDto));
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    private void validate(ItemDto itemDto) throws ValidationException {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Not valid");
        }
    }
}
