package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class BookingNotFoundException extends Exception implements ApiSubError {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
