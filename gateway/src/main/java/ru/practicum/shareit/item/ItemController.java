package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RequestMapping("/items")
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid ItemRequestDto itemDto) throws Exception {
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestUpdateDto itemDto, @PathVariable long id) throws Exception {
        return itemClient.update(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id) throws Exception {
        return itemClient.delete(id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id,
                           @RequestHeader("X-Sharer-User-Id") long userId) throws Exception {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                @Min(1) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text") String text,
                                @PositiveOrZero @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                @Min(1) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                 @RequestBody@Valid CommentRequestDto commentDto) throws Exception {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
