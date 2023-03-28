package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) throws Exception {
        validate(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item i = itemRepository.save(item);
        return ItemMapper.toItemDto(i);
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) throws Exception {

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
    public ItemDto delete(long id, long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item itemToDelete = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (!Objects.equals(itemToDelete.getOwner().getId(), userId)) {
            throw new UnauthorizedException("User can not delete this item!");
        }
        itemRepository.deleteById(id);
        return ItemMapper.toItemDto(itemToDelete);
    }

    @Override
    public ItemDto getById(long id, long userId) throws Exception {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item);
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
        setLastAndNextBookingForItem(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(long userId) {
        return itemRepository.findAll().stream().filter(item -> Objects.equals(item.getOwner()
                        .getId(), userId)).map(item -> {
                    if (item.getOwner().getId().equals(userId)) {
                        ItemDto itemDto = ItemMapper.toItemDto(item);
                        setLastAndNextBookingForItem(itemDto);
                        return itemDto;
                    } else {
                        return ItemMapper.toItemDto(item);
                    }
                }).sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    private void setLastAndNextBookingForItem(ItemDto itemDto) {
        List<Booking> allApprovedBookingsForItem = bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId())
                        && booking.getStatus().equals(BookingState.APPROVED))
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        Booking last = allApprovedBookingsForItem.stream()
                .filter(booking -> booking.getEnd().isBefore(now)
                        || (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking next = allApprovedBookingsForItem.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        itemDto.setLastBooking(last == null ? null : BookingMapper.toBookingDto(last));
        itemDto.setNextBooking(next == null ? null : BookingMapper.toBookingDto(next));
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
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) throws Exception {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Empty comment");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        LocalDateTime now = LocalDateTime.now();
        boolean userBookedItem = bookingRepository.findAllByBookerId(userId).stream()
                .anyMatch(booking -> Objects.equals(booking.getItem().getId(), item.getId())
                        && booking.getStatus().equals(BookingState.APPROVED)
                        && booking.getStart().isBefore(now));
        if (!userBookedItem) {
            throw new ValidationException("Unauthorized");
        }
        Comment comment = new Comment();
        comment.setCreated(now);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        comment.setItem(item);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private void validate(ItemDto itemDto) throws ValidationException {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Not valid");
        }
    }
}
