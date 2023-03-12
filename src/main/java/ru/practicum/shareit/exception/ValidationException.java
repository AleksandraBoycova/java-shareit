package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class ValidationException extends Exception implements ApiSubError {

    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public ValidationException(Object object, String message, String field, Object rejectedValue) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.object = object.getClass().getName();
        this.message = message;
    }

    public ValidationException(String message) {
        super(message);
    }
}
