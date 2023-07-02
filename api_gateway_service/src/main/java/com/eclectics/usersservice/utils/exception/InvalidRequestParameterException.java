package com.eclectics.usersservice.utils.exception;

public class InvalidRequestParameterException extends RuntimeException{

    public InvalidRequestParameterException(String message) {
        super(message);
    }
}
