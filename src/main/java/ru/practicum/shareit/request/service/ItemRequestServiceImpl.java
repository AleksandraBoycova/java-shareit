package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private UserRepository        userRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository        itemRepository;

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
        itemRequest.setRequestor(user);
        itemRequest.setDescription(requestDto.getDescription());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        List<Item>  items = itemRepository.findAllByRequestId(savedRequest.getId());
        savedRequest.setItems(items.isEmpty() ? null : new HashSet<>(itemRepository.findAllByRequestId(savedRequest.getId())));
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long id) throws Exception {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Item request not found"));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) throws UserNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (size != null) {
            int page = from / size;

            final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("created").descending());
            List<ItemRequest> result      = itemRequestRepository.findAllByRequestorId(userId, pageRequest).getContent();
            return result.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    private void validate(ItemRequestDto itemRequestDto) throws ValidationException {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Description is null");
        }
    }
}
