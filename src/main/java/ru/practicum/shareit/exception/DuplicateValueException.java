package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class DuplicateValueException extends Exception implements ApiSubError {
    public DuplicateValueException(String message) {
        super(message);
    }
}
