package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateValueException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) throws ValidationException, DuplicateValueException {
        return service.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody @Valid UserDto userDto) throws UserNotFoundException, ValidationException, DuplicateValueException {
        return service.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable long id) throws UserNotFoundException {
        return service.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) throws UserNotFoundException {
        return service.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }
}
