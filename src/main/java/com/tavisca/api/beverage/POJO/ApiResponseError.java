package com.tavisca.api.beverage.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseError extends ApiResponse{
    HttpStatus code;
    Object message;

    public ApiResponseError(Object error, HttpStatus code) {
        this.message = error;
        this.code = code;
        this.status = "error";
    }
}
