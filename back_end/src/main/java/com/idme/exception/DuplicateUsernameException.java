package com.idme.exception;

public class DuplicateUsernameException extends BaseException{

    public DuplicateUsernameException() {
    }

    public DuplicateUsernameException(String msg) {
        super(msg);
    }
}
