package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id")
    private User          requestor;
    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany (mappedBy = "request", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Item> items;
}
