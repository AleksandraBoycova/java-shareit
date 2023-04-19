package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) throws Exception {
        validate(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElse(null));
        }
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
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
        LocalDateTime now = LocalDateTime.now();

        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item);
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Booking> lastBookingsList = bookingRepository.findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc(List.of(item.getId()), BookingState.APPROVED, now);
        List<Booking> nextBookingList = bookingRepository.findAllByItemIdInAndStatusAndStartAfterOrderByStart(List.of(item.getId()), BookingState.APPROVED, now);
        itemDto.setLastBooking(lastBookingsList.isEmpty() ? null : BookingMapper.toBookingDto(lastBookingsList.get(0)));
        itemDto.setNextBooking(nextBookingList.isEmpty() ? null : BookingMapper.toBookingDto(nextBookingList.get(0)));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> itemsByOwnerId = itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size)).getContent();
        List<Long> ownerItemIds = itemsByOwnerId.stream().map(Item::getId).collect(toList());
        List<Booking> lastBookingsList = bookingRepository.findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter(ownerItemIds, BookingState.APPROVED, now, now, now);
        List<Booking> nextBookingList = bookingRepository.findAllByItemIdInAndStatusAndStartAfter(ownerItemIds, BookingState.APPROVED, now);
        Map<Long, List<Booking>> last = lastBookingsList.stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.collectingAndThen(toList(),
                e -> e.stream().sorted(Comparator.comparing(Booking::getStart))
                        .collect(toList()))));
        Map<Long, List<Booking>> next = nextBookingList.stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.collectingAndThen(toList(),
                e -> e.stream().sorted(Comparator.comparing(Booking::getStart))
                        .collect(toList()))));
        List<ItemDto> itemDtos = new ArrayList<>();
        itemsByOwnerId.forEach(item -> {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setLastBooking(last.get(item.getId()) == null ? null : BookingMapper.toBookingDto(last.get(item.getId()).get(last.get(item.getId()).size() - 1)));
            itemDto.setNextBooking(next.get(item.getId()) == null ? null : BookingMapper.toBookingDto(next.get(item.getId()).get(0)));
            itemDtos.add(itemDto);
        });
        return itemDtos.stream().sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, PageRequest.of(from / size, size, Sort.by("id"))).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
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
