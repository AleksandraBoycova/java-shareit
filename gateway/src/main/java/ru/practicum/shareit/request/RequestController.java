package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.dto.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;


    @RequestMapping(path = "/requests")
    @Controller
    @RequiredArgsConstructor
    @Slf4j
    @Validated
    public class RequestController {

        private final RequestClient requestClient;

        @PostMapping
        public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody RequestDto itemRequestDto) throws Exception {
            return RequestClient.create(itemRequestDto, userId);
        }

        @GetMapping
        public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws Exception {
            return RequestClient.getAllOwnRequests(userId);
        }

        @GetMapping("/all")
        public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                                       @RequestParam(value = "size", required = false) Integer size) throws Exception {
            return RequestClient.getAllUserRequests(userId, from, size);
        }

        @GetMapping("/{requestId}")
        public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) throws Exception {
            return RequestClient.getById(userId, requestId);
        }
    }
}
