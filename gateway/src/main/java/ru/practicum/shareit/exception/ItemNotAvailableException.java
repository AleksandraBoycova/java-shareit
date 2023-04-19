package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class ItemNotAvailableException extends Exception implements ApiSubError {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
