package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item save(Item item);
    Item update(Item item);

    Item delete(long id) throws ItemNotFoundException;

    Item getById(long id) throws ItemNotFoundException;

    List<Item> getAll();

}
