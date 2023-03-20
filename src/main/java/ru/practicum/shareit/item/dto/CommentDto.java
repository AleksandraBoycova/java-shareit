package ru.practicum.shareit.item.dto;


import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private Long item;
    private Long author;
    private LocalDate created;
}
