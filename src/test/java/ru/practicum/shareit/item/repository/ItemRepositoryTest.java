package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(4L,
                PageRequest.of(0, 10, Sort.by("id"))).getContent();
        assertEquals(3, items.size());
        assertEquals(2, items.get(0).getId());
        assertEquals(3, items.get(1).getId());
        assertEquals(5, items.get(2).getId());
    }

    @Test
    void findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrueOrderById() {
        List<Item> items = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue("АккУМулятОРНая",
                "АккУМулятОРНая",
                PageRequest.of(0, 10, Sort.by("id"))).getContent();
        assertEquals(2, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals(2, items.get(1).getId());
    }

    @Test
    void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(2L);
        assertEquals(1, items.size());
        assertEquals(3, items.get(0).getId());
    }

    @Test
    void findAllByRequestIdIn() {
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(2L, 1L));
        assertEquals(2, items.size());
        assertEquals(3, items.get(0).getId());
        assertEquals(5, items.get(1).getId());
    }
}