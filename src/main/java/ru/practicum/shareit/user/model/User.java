package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(name = "email", unique = true, nullable = false, length = 512)
    private String email;

}
