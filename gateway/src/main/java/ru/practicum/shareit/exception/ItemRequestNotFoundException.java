package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class ItemRequestNotFoundException extends Exception implements ApiSubError {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
