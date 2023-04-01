package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllByBookerId() {
        User user1 = new User();
        User user2 = new User();
        Booking booking1 = new Booking();
    }

    @Test
    void findAllByBookerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStatusInAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStatusInAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemIdInAndStatusAndStartBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByItemIdInAndStatusAndStartAfterOrderByStart() {
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
    }

    @Test
    void findAllByItemIdInAndStatusAndEndBeforeOrStartBeforeAndEndAfter() {
    }

    @Test
    void findAllByItemIdInAndStatusAndStartAfter() {
    }
}