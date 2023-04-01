package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;

    private LocalDate created;

    private Set<ItemDtoShort> items;
}

