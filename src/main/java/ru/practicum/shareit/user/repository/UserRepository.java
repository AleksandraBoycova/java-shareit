package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);
    User update(User user);

    User delete(long id) throws UserNotFoundException;

    User getById(long id) throws UserNotFoundException;

    List<User> getAll();
}
