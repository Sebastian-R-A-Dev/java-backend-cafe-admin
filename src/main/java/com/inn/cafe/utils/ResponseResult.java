package com.inn.cafe.utils;


public class ResponseResult {
    private String message;
    private Object result;
    private Boolean flagResult;

    private ResponseResult(){}

    public ResponseResult(String message, Boolean flagResult) {
        this.message = message;
        this.flagResult = flagResult;
    }

    public ResponseResult(String message, Object result, Boolean flagResult) {
        this.message = message;
        this.result = result;
        this.flagResult = flagResult;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public Boolean getFlagResult() {
        return flagResult;
    }
}
