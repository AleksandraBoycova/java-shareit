package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class UserNotFoundException extends Exception implements ApiSubError {
    public UserNotFoundException(String message) {
        super(message);
    }
}
