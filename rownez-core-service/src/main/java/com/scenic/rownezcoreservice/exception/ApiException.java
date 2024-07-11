package com.scenic.rownezcoreservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class ApiException extends RuntimeException{
    private HttpStatus status;
    public ApiException (String message){
        super(message);
    }
    public ApiException (Throwable throwable){
        super(throwable);
    }
    public ApiException (String message, Throwable cause) {
        super(message, cause);
    }
    public ApiException (String message, HttpStatus status) {
        super (message);
        this.status = status;
    }

}
