package com.tavisca.api.beverage.exception;


import com.tavisca.api.beverage.POJO.ApiResponse;
import com.tavisca.api.beverage.POJO.ApiResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllException(Exception e) {
        return new ResponseEntity<>(new ApiResponseError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderRequestException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(InvalidOrderRequestException e) {
        return new ResponseEntity<>(new ApiResponseError(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
    }
}
