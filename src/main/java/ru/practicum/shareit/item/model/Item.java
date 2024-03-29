package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1024)
    private String description;
    private boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonBackReference
    private ItemRequest request;

    @JsonManagedReference
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Comment> comments;
}