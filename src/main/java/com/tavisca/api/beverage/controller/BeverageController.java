package com.tavisca.api.beverage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tavisca.api.beverage.POJO.ApiResponse;
import com.tavisca.api.beverage.POJO.ApiResponseSuccess;
import com.tavisca.api.beverage.POJO.OrderRequest;
import com.tavisca.api.beverage.exception.InvalidOrderRequestException;
import com.tavisca.api.beverage.service.CoreBeverageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api")
public class BeverageController {

    @Autowired
    private CoreBeverageService coreService;

    @ApiOperation(value = "get order details")
    @RequestMapping(method = POST, value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> evaluateTotalPrice(@RequestBody OrderRequest order, HttpServletRequest request)
            throws JsonProcessingException, UnsupportedEncodingException, GeneralSecurityException, InvalidOrderRequestException {

        BigDecimal totalBill = coreService.getOrderTotalBill(order);
        return new ResponseEntity<>(new ApiResponseSuccess(totalBill), HttpStatus.OK);
    }
}

