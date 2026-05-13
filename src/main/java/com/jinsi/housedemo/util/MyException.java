package com.jinsi.housedemo.util;

public class MyException extends RuntimeException {
    private int code;
    public  MyException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
