package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApplicationError {

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime timestamp;

    private String message;

    private List<ApiSubError> errors;

    private ApplicationError() {
        timestamp = LocalDateTime.now();
        errors = new ArrayList<>();
    }

    public ApplicationError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApplicationError(HttpStatus status, Exception ex) {
        this();
        this.status = status;
        this.message = "Ошибка валидации";
        if (ex instanceof ApiSubError) {
            errors.add((ApiSubError) ex);
        }
    }

    public ApplicationError(HttpStatus status, String message, Exception ex) {
        this();
        this.status = status;
        this.message = message;
        if (ex instanceof ApiSubError) {
            errors.add((ApiSubError) ex);
        }
    }
}
