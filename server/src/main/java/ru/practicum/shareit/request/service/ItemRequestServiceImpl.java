package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, Long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        validate(requestDto);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription(requestDto.getDescription());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        savedRequest.setItems(itemRepository.findAllByRequestId(savedRequest.getId()));
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long id) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new ItemRequestNotFoundException("Item request not found"));
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        itemRequest.setItems(items);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllOwnRequests(Long userId) throws UserNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<Item> itemsByRequestIds = itemRepository.findAllByRequestIdIn(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
        Map<Long, List<Item>> mappedItemsByRequestIds = itemsByRequestIds.stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        itemRequests.forEach(itemRequest -> {
            List<Item> items = mappedItemsByRequestIds.get(itemRequest.getId());
            itemRequest.setItems(items == null ? new ArrayList<>() : items);
        });
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(Long userId, Integer from, Integer size) throws UserNotFoundException, ValidationException {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (size != null && size == 0) {
            throw new ValidationException("Error");
        }
        if (size != null && from != null) {
            int page = from / size;

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("created").descending());

            List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest).getContent();
            List<Item> itemsByRequestIds = itemRepository.findAllByRequestIdIn(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
            Map<Long, List<Item>> mappedItemsByRequestIds = itemsByRequestIds.stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));
            itemRequests.forEach(itemRequest -> {
                List<Item> items = mappedItemsByRequestIds.get(itemRequest.getId());
                itemRequest.setItems(items == null ? new ArrayList<>() : items);
            });
            return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private void validate(ItemRequestDto itemRequestDto) throws ValidationException {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Description is null");
        }
    }
}
