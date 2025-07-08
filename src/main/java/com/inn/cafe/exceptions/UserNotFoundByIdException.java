package com.inn.cafe.exceptions;

public class UserNotFoundByIdException extends Exception {
    public UserNotFoundByIdException(String msj) {
        super(msj);
    }
}
