package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestUpdateDto {
   @NotBlank
    private String           name;
    @NotBlank
    private String           description;
    private Boolean          available;
    private Long             requestId;
}
