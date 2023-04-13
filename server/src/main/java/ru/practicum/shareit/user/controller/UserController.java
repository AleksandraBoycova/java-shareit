package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) throws Exception {
        return service.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) throws Exception {
        return service.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable long id) throws Exception {
        return service.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) throws Exception {
        return service.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }
}
