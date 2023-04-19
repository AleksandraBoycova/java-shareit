package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;


    @RequestMapping(path = "/requests")
    @Controller
    @RequiredArgsConstructor
    @Slf4j
    @Validated
    public class RequestController {

        private final RequestClient requestClient;

        @PostMapping
        public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid RequestDto itemRequestDto) throws Exception {
            return requestClient.create(itemRequestDto, userId);
        }

        @GetMapping
        public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) throws Exception {
            return requestClient.getAllOwnRequests(userId);
        }

        @GetMapping("/all")
        public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                                       @RequestParam(value = "size", required = false) Integer size) throws Exception {
            return requestClient.getAllUserRequests(userId, from, size);
        }

        @GetMapping("/{requestId}")
        public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) throws Exception {
            return requestClient.getById(userId, requestId);
        }
    }

