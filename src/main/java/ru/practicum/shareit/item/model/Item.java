package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;


/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    @Column(name = "item_name", nullable = false)
    private String        name;
    @Column(name = "description", nullable = false, length = 1024)
    private String        description;
    private boolean       available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User          owner;
    //private ItemRequest request;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments;
}
