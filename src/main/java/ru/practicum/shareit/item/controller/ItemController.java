package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) throws UserNotFoundException, ValidationException {
        return service.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto, @PathVariable long id) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException {
        return service.update(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException {
        return service.delete(id, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) throws ItemNotFoundException {
        return service.getById(id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        return service.search(text);
    }

   @PostMapping("{itemId}/comment")
    public ItemDto addComment (@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                               @RequestBody CommentDto commentDto) throws UserNotFoundException, UnauthorizedException, ItemNotFoundException {
        return service.addComment(userId, itemId, commentDto);
   }
}
