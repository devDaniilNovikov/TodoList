package dn.tasktracker.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RedisTransactionException extends RuntimeException{
    public RedisTransactionException(String message) {
        super(message);
    }
}
