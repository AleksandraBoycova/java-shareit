package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Entity
@Table (name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_start")
    private LocalDate start;
    @Column(name = "booking_end")
    private LocalDate end;
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(optional = false)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingState status;
}
