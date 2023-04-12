package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest extends BaseTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(1L);
        assertEquals(1, itemRequests.size());
        assertEquals(1, itemRequests.get(0).getId());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(1L,
                PageRequest.of(0, 10)).getContent();
        assertEquals(2, itemRequests.size());
        assertEquals(2, itemRequests.get(0).getId());
        assertEquals(3, itemRequests.get(1).getId());
    }

    @Test
    void save() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(buildUser(2L, "email", "name"));
        itemRequest.setDescription("item request description");
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        assertEquals(4, savedItemRequest.getId());
    }

    @Test
    void findAll() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        assertEquals(3, itemRequests.size());
    }

    @Test
    void findById() {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(2L);
        assertTrue(itemRequest.isPresent());
        assertEquals("Хотел бы воспользоваться мясорубкой", itemRequest.get().getDescription());
    }
}