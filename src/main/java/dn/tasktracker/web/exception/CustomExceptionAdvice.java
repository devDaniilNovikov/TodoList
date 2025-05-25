package dn.tasktracker.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorBody> handleTaskNotFoundException(WebRequest webRequest, TaskNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorBody.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .description(ex.getLocalizedMessage())
                        .path(webRequest.getDescription(false))
                        .build());
    }

    @ExceptionHandler(TaskAlreadyExistsException.class)
    public ResponseEntity<ErrorBody> handleTaskAlreadyExistsException(WebRequest request, TaskAlreadyExistsException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorBody.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .description(ex.getLocalizedMessage())
                        .path(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorBody> handleUserAlreadyExistsException(WebRequest request, UserAlreadyExistsException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorBody.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .description(ex.getLocalizedMessage())
                        .path(request.getDescription(false))
                        .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorBody> handleUserNotFoundException(WebRequest request, UserNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorBody.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .description(ex.getLocalizedMessage())
                        .path(request.getDescription(false))
                        .build());
    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorBody> handleException(WebRequest request, Exception ex){
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(ErrorBody.builder()
//                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                        .description(ex.getLocalizedMessage())
//                        .path(request.getDescription(false))
//                        .build());
//    }
}



