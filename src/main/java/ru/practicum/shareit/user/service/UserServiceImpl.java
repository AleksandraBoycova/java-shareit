package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateValueException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) throws ValidationException, DuplicateValueException {
        validateUser(userDto);
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        User u = userRepository.save(user);
        return UserMapper.toUserDto(u);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) throws UserNotFoundException, DuplicateValueException, ValidationException {
        User userToUpdate = userRepository.getById(userId);
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            boolean emailExists = userRepository.findAll().stream().filter(user -> !Objects.equals(user.getId(), userId)).map(User::getEmail).anyMatch(email -> email.equals(userDto.getEmail()));

            if (emailExists) {
                throw new DuplicateValueException("Email exists!");
            }
            userToUpdate.setEmail(userDto.getEmail());
        }
        User user = userRepository.save(userToUpdate);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long id) throws UserNotFoundException {
        User user = userRepository.getById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private void validateUser(UserDto userDto) throws ValidationException, DuplicateValueException {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email is null!");
        }
        boolean containsEmail = userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
        if (containsEmail) {
            throw new DuplicateValueException("Email exists");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Name is null or empty");
        }
    }
}
