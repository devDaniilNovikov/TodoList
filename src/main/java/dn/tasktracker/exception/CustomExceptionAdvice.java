package dn.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorBody> handleTaskNotFoundException(WebRequest webRequest,
                                                                 TaskNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorBody.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message(ex.getLocalizedMessage())
                        .path(webRequest.getDescription(false))
                        .build()
                );
    }
}



