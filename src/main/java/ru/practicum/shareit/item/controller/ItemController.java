package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity <ItemDto> create (@RequestBody ItemDto itemDto) {
        ItemDto item = service.create(itemDto, null);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    public ResponseEntity <ItemDto> update (@RequestBody ItemDto itemDto, @PathVariable Long id) {
    return null;
    }
    @DeleteMapping("/{id}")
    public ResponseEntity <ItemDto> delete (@PathVariable Long id) {
        return null;
    }

   @GetMapping("/{id}")
    public ResponseEntity <ItemDto> getById (@PathVariable Long id) {
        return null;
    }
    @GetMapping
    public ResponseEntity <List<ItemDto>> getAll () {
        return null;
    }

   @GetMapping("/search")
   public ResponseEntity <List<ItemDto>> search (@RequestParam(name = "text") String text) {
        return null;
    }
}
