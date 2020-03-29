package com.tavisca.api.beverage.exception;

import com.tavisca.api.beverage.constants.Errors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidOrderRequestException extends RuntimeException {

    private final HttpStatus code;

    public InvalidOrderRequestException(Errors message, HttpStatus code) {
        super(message.getErrorMessage());
        this.code = code;
    }
}
