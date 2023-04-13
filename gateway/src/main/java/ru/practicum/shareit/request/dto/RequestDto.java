package ru.practicum.shareit.request.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RequestDto {
   @NotBlank
   @NotNull
    private String description;
}
