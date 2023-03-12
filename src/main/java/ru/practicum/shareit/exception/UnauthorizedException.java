package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class UnauthorizedException extends Exception implements ApiSubError {
    public UnauthorizedException(String message) {
        super(message);
    }
}
