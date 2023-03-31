package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setId(userDto.getId());
        return user;
    }

    public static UserDtoShort toUserDtoShort(User user) {
        UserDtoShort userDtoShort = new UserDtoShort();
        userDtoShort.setId(user.getId());
        userDtoShort.setName(user.getName());
        return userDtoShort;
    }
}
