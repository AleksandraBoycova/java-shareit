package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class ItemNotFoundException extends Exception implements ApiSubError {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
