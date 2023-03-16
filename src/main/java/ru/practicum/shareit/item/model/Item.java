package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table (name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "item_name", nullable = false)
    private String name;
    @Column (name = "description", nullable = false, length = 1024)
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;

}
