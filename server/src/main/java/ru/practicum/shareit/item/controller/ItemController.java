package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) throws Exception {
        return service.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto, @PathVariable long id) throws Exception {
        return service.update(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id) throws Exception {
        return service.delete(id, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id,
                           @RequestHeader("X-Sharer-User-Id") long userId) throws Exception {
        return service.getById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                @Min(1) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text,
                                @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                @Min(1) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return service.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) throws Exception {
        return service.addComment(userId, itemId, commentDto);
    }
}
