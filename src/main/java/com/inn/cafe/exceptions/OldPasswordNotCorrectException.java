package com.inn.cafe.exceptions;

public class OldPasswordNotCorrectException extends Exception{
    public OldPasswordNotCorrectException(String msj) {
        super(msj);
    }
}
