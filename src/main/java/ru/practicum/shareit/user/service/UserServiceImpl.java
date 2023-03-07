package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private static Map<Long, User> STORAGE = new HashMap<>();
    private static long COUNTER = 1;


    @Override
    public UserDto create(UserDto userDto) {
      validateUser(userDto);
       User user = new User();
       user.setId(COUNTER++);
       user.setName(userDto.getName());
       user.setEmail(userDto.getEmail());
       STORAGE.put(user.getId(), user);
        return userDto;
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (!STORAGE.containsKey(userId)) {
            throw new RuntimeException();
        }
        User userToUpdate = STORAGE.get(userId);
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        return userDto;
    }

    @Override
    public UserDto delete(Long id) {
        if (!STORAGE.containsKey(id)) {
            throw new RuntimeException();
        }
        User userToDelete = STORAGE.get(id);
        STORAGE.remove(id);

        return UserMapper.toUserDto(userToDelete);
    }

    @Override
    public UserDto getById(Long id) {
        if (!STORAGE.containsKey(id)) {
            throw new RuntimeException();
        }
        User user = STORAGE.get(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return STORAGE.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private void validateUser (UserDto userDto) {
       if (userDto.getEmail() == null) {
           throw new RuntimeException();
       }
        boolean containsEmail = STORAGE.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
       if (containsEmail) {
           throw new RuntimeException();
       }
       if (userDto.getName() == null || userDto.getName().isBlank()) {
           throw new RuntimeException();
       }
    }
}
