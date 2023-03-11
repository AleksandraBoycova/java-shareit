package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private static Map<Long, Item> STORAGE = new HashMap<>();
    private static long COUNTER = 1;

    @Override
    public Item save(Item item) {
        item.setId(COUNTER++);
        STORAGE.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        STORAGE.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(long id) throws ItemNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new ItemNotFoundException("Item not found!");
        }
        Item item = STORAGE.get(id);
        STORAGE.remove(id);
        return item;
    }

    @Override
    public Item getById(long id) throws ItemNotFoundException {
        if (!STORAGE.containsKey(id)) {
            throw new ItemNotFoundException("Item not found!");
        }
        return STORAGE.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(STORAGE.values());
    }
}
