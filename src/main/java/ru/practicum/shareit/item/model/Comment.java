package ru.practicum.shareit.item.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    private String        text;
    @ManyToOne
    @JoinColumn(name = "item")
    @JsonBackReference
    private Item          item;
    @OneToOne
    @JoinColumn(name = "author")
    private User          author;
    private LocalDateTime created;
}
