package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save() {
        User user      = buildUser(null, "user78@mail.com", "user78");
        User savedUser = userRepository.save(user);
        assertEquals(5, savedUser.getId());
    }

    @Test
    void saveDuplicateEmail() {
        User user = buildUser(null, "user@user.com", "user78");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void findAll() {
        List<User> itemRequests = userRepository.findAll();
        assertEquals(4, itemRequests.size());
    }

    @Test
    void findById() {
        Optional<User> user = userRepository.findById(4L);
        assertTrue(user.isPresent());
        assertEquals("practicum@yandex.ru", user.get().getEmail());
    }

}