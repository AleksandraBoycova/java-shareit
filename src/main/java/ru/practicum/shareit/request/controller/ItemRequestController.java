package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) throws Exception {
        return service.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws Exception {
        return service.getAll(userId, null, null);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @Min(1) @RequestParam(value = "size", defaultValue = "20") Integer size) throws Exception {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) throws Exception {
        return service.getById(userId, requestId);
    }
}
