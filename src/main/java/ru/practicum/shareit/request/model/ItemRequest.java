package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    @CreationTimestamp
    private LocalDate created;

    @OneToMany (mappedBy = "request", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Item> items;
}
