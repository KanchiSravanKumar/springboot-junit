package com.tavisca.api.beverage.POJO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ApiResponseSuccess extends ApiResponse {
    private BigDecimal total;

    public ApiResponseSuccess(BigDecimal total) {
        this.status = "success";
        this.total = total;
    }
}
