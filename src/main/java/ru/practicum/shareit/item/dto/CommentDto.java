package ru.practicum.shareit.item.dto;


import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long          id;
    private String        text;
    private String        authorName;
    private LocalDateTime created;
}
