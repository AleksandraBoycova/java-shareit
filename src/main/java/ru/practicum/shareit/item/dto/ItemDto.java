package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private List<CommentDto> comments;
}
