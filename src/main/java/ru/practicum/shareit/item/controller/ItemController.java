package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
    public ResponseEntity <ItemDto> create (@RequestHeader ("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemDto itemDto) throws UserNotFoundException, ValidationException {
        ItemDto item = service.create(itemDto, userId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    public ResponseEntity <ItemDto> update (@RequestHeader ("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemDto itemDto, @PathVariable Long id) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException {
        ItemDto item = service.update(id, itemDto, userId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity <ItemDto> delete (@RequestHeader ("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long id) throws UserNotFoundException, ValidationException, ItemNotFoundException, UnauthorizedException {
        ItemDto item = service.delete(id, userId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

   @GetMapping("/{id}")
    public ResponseEntity <ItemDto> getById (@PathVariable Long id) throws ItemNotFoundException {
       ItemDto item = service.getById(id);
       return new ResponseEntity<>(item, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity <List<ItemDto>> getAll (@RequestHeader ("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = service.getAll(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

   @GetMapping("/search")
   public ResponseEntity <List<ItemDto>> search (@RequestParam(name = "text") String text) {
       List<ItemDto> items = service.search(text);
       return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
