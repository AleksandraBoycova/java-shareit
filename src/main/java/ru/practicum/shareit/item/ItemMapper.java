package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setRequestId(item.getRequest() == null ? null : item.getRequest().getId());
        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }
        return itemDto;

    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;

    }

    public static ItemDtoShort toItemDtoShort(Item item) {
        ItemDtoShort itemDtoShort = new ItemDtoShort();
        itemDtoShort.setId(item.getId());
        itemDtoShort.setName(item.getName());
        return itemDtoShort;
    }
}
