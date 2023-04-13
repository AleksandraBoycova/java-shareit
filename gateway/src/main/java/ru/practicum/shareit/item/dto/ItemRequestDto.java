package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ItemRequestDto {
   @NotNull
   @NotBlank
    private String           name;
    @NotNull
    @NotBlank
    private String           description;
    @NotNull
    private Boolean          available;
    private Long             requestId;
}
