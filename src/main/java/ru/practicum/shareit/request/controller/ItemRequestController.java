package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto create (@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) throws Exception {
        return service.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests () {
        return service.getAll();
    }

    @GetMapping ("/all")
    public List<ItemRequestDto> getAllItemRequests (@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam("from") Long from, @RequestParam("size") Long size) {
        return service.getAll(from, size);
    }

    @GetMapping ("/{requestId}")
    public ItemRequestDto getItemRequest (@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) throws Exception {
        return service.getById(userId, requestId);
    }
}
