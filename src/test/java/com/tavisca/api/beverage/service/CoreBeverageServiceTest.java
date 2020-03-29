package com.tavisca.api.beverage.service;

import com.tavisca.api.beverage.POJO.OrderRequest;
import com.tavisca.api.beverage.exception.InvalidOrderRequestException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CoreBeverageService.class})
class CoreBeverageServiceTest {


    @Resource
    private CoreBeverageService coreBeverageService;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    void getOrderTotalBill_validRequestWithoutExclusion_correctTotalBill() throws InvalidOrderRequestException {
        OrderRequest order = new OrderRequest(Arrays.asList("Coffee,Tea"));
        BigDecimal total = coreBeverageService.getOrderTotalBill(order);
        Assert.assertEquals(new BigDecimal(9), total);
    }

    @Test
    void getOrderTotalBill_validRequestWithExclusion_correctTotalBill() throws InvalidOrderRequestException {
        OrderRequest order = new OrderRequest(Arrays.asList("Coffee","Tea, -milk"));
        BigDecimal total1 = coreBeverageService.getOrderTotalBill(order);
        Assert.assertEquals(new BigDecimal(8), total1);
    }

    @Test
    void getOrderTotalBill_validRequestWithMultipleItemAndExclusion_correctTotalBill() throws InvalidOrderRequestException {
        OrderRequest order = new OrderRequest(Arrays.asList("Coffee, -milk", "Tea, -milk"));
        BigDecimal total2 = coreBeverageService.getOrderTotalBill(order);
        Assert.assertEquals(new BigDecimal(7), total2);
    }

    @Test
    void getOrderTotalBill_invalidRequestWithMenuItemExclusion_invalidOrderRequestException() throws InvalidOrderRequestException{
        OrderRequest order = new OrderRequest(Arrays.asList("Coffee","-Chai, -milk"));
        Assertions.assertThrows(InvalidOrderRequestException.class, () -> {
            coreBeverageService.getOrderTotalBill(order);
        });
    }


    @Test
    void getOrderTotalBill_invalidRequestWithNoMenuItem_invalidOrderRequestException() throws InvalidOrderRequestException{
        OrderRequest order = new OrderRequest(Arrays.asList("-milk"));
        Assertions.assertThrows(InvalidOrderRequestException.class, () -> {
            coreBeverageService.getOrderTotalBill(order);
        });
    }

    @Test
    void getOrderTotalBill_invalidRequestWithEmptyItem_invalidOrderRequestException() throws InvalidOrderRequestException{
        OrderRequest order = new OrderRequest(Arrays.asList(""));
        Assertions.assertThrows(InvalidOrderRequestException.class, () -> {
            coreBeverageService.getOrderTotalBill(order);
        });
    }

    @Test
    void getOrderTotalBill_invalidRequestExcludeAllIngredients_invalidOrderRequestException() throws InvalidOrderRequestException{
        OrderRequest order = new OrderRequest(Arrays.asList("Coffee, -coffee, -milk, -sugar, -water"));
        Assertions.assertThrows(InvalidOrderRequestException.class, () -> {
            coreBeverageService.getOrderTotalBill(order);
        });
    }
}