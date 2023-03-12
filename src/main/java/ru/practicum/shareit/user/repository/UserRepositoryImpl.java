package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {

    private static Map<Long, User> STORAGE = new HashMap<>();
    private static long COUNTER = 1;

    @Override
    public User save(User user) {
        user.setId(COUNTER++);
        STORAGE.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        STORAGE.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(long id) throws UserNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new UserNotFoundException("User not found");
        }
        User userToDelete = STORAGE.get(id);
        STORAGE.remove(id);
        return userToDelete;
    }

    @Override
    public User getById(long id) throws UserNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new UserNotFoundException("User not found");
        }
        User user = STORAGE.get(id);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(STORAGE.values());
    }
}
