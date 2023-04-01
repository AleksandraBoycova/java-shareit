package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found"));
        validate(requestDto);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription(requestDto.getDescription());
        itemRequestRepository.save(itemRequest);
        return null;
    }

    @Override
    public ItemRequestDto getById(long id, long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Item not found"));
        if (!Objects.equals(itemRequest.getRequestor().getId(), user.getId())) {
            throw new ValidationException("Validation error");
        }
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        return null;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Long from, Long size) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return null;
    }

    private void validate(ItemRequestDto itemRequestDto) throws ValidationException {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Description is null");
        }
    }
}
