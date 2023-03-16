package ru.practicum.shareit.item.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table (name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
