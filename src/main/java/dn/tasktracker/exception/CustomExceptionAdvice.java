package dn.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorBody> handleTaskNotFoundException(HttpStatus httpStatus,
                                                                 WebRequest webRequest,
                                                                 TaskNotFoundException ex){
        return ResponseEntity.ok(
                ErrorBody.builder()
                        .code(httpStatus.value())
                        .message(ex.getLocalizedMessage())
                        .description(webRequest.getDescription(true))
                        .path(webRequest.getContextPath())
                        .build()
        );
    }
}
