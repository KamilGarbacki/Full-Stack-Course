package com.kgarbacki.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateResourceExeption extends RuntimeException{

    public DuplicateResourceExeption(String message) {
        super(message);
    }
}
