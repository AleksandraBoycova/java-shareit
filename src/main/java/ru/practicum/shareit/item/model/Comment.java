package ru.practicum.shareit.item.model;


import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table (name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDate created;
}
