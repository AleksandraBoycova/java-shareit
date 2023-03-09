package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApplicationError> handleValidationException(ValidationException e) {
        ApplicationError applicationError = new ApplicationError(HttpStatus.BAD_REQUEST, e);
        log.error("Validation Exception Thrown");
        return new ResponseEntity<>(applicationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ItemNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ApplicationError> handleNotFoundException(Exception e) {
        ApplicationError applicationError = new ApplicationError(HttpStatus.NOT_FOUND, "Объект не найден", e);
        log.error("Not found exception thrown");
        return new ResponseEntity<>(applicationError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler()
    public ResponseEntity<ApplicationError> handleException(Exception e) {
        ApplicationError applicationError = new ApplicationError(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка", e);
        log.error("Unexpected exception thrown");
        return new ResponseEntity<>(applicationError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApplicationError> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        List<FieldError> allErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : allErrors) {
            errorMessage.append("Field: ").append(error.getField());
            errorMessage.append(", rejected value: ").append(error.getRejectedValue());
            errorMessage.append(" error: ").append(error.getDefaultMessage());
        }
        ApplicationError applicationError = new ApplicationError(HttpStatus.BAD_REQUEST, "Ошибка валидации. " + errorMessage, e);
        MethodParameter parameter = e.getParameter();
        parameter.getAnnotatedElement();
        log.error("Method Argument Not Valid Exception");
        return new ResponseEntity<>(applicationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApplicationError> handleUnauthorizedException(UnauthorizedException e) {
        ApplicationError applicationError = new ApplicationError(HttpStatus.FORBIDDEN, "Нет доступа", e);
        log.error("Unauthorized exception thrown");
        return new ResponseEntity<>(applicationError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateValueException.class)
    public ResponseEntity<ApplicationError> handleDuplicateValueException(DuplicateValueException e) {
        ApplicationError applicationError = new ApplicationError(HttpStatus.CONFLICT, "Конфликт", e);
        log.error("Duplicate Value exception thrown");
        return new ResponseEntity<>(applicationError, HttpStatus.CONFLICT);
    }
}
