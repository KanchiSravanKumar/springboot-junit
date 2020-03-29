package com.tavisca.api.beverage.service;

import com.tavisca.api.beverage.POJO.OrderRequest;
import com.tavisca.api.beverage.constants.Errors;
import com.tavisca.api.beverage.constants.MenuItemIngredients;
import com.tavisca.api.beverage.constants.MenuItems;
import com.tavisca.api.beverage.exception.InvalidOrderRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CoreBeverageService {
    // for checking if all ingredients are excluded

    private static final Map<MenuItems, String> mapOfBevToCosts = createMapOfBevAndCosts();
    private static final Map<MenuItemIngredients, BigDecimal> mapOfIngredientsToCosts = createMapOfIngredientsAndCosts();

    private static Map<MenuItems, String> createMapOfBevAndCosts() {
        Map<MenuItems, String> mapOfMenuItemsAndCosts = new HashMap<>();
        mapOfMenuItemsAndCosts.put(MenuItems.COFFEE, "coffee,milk,sugar,water,5" );
        mapOfMenuItemsAndCosts.put(MenuItems.CHAI, "tea,milk,sugar,water,4");
        mapOfMenuItemsAndCosts.put(MenuItems.TEA, "tea,milk,sugar,water,4");
        mapOfMenuItemsAndCosts.put(MenuItems.BANANA_SMOOTHIE, "banana,milk,sugar,water,6" );
        mapOfMenuItemsAndCosts.put(MenuItems.MOJITO, "lemon,sugar,water,soda,mint,7");
        mapOfMenuItemsAndCosts.put(MenuItems.STRAWBERRY_SHAKE, "strawberries,sugar,milk,water,7.5");
        return Collections.unmodifiableMap(mapOfMenuItemsAndCosts);
    }

    private static Map<MenuItemIngredients, BigDecimal> createMapOfIngredientsAndCosts() {
        Map<MenuItemIngredients, BigDecimal> mapOfMenuItemsAndCosts = new HashMap<>();
        mapOfMenuItemsAndCosts.put(MenuItemIngredients.MILK, new BigDecimal(1));
        mapOfMenuItemsAndCosts.put(MenuItemIngredients.SUGAR, new BigDecimal(0.5));
        mapOfMenuItemsAndCosts.put(MenuItemIngredients.SODA, new BigDecimal(0.5));
        mapOfMenuItemsAndCosts.put(MenuItemIngredients.MINT, new BigDecimal(0.5));
        mapOfMenuItemsAndCosts.put(MenuItemIngredients.WATER, new BigDecimal(0.5));
        return Collections.unmodifiableMap(mapOfMenuItemsAndCosts);
    }


    public BigDecimal getOrderTotalBill(OrderRequest order) throws InvalidOrderRequestException {
        BigDecimal total = BigDecimal.ZERO;
        MenuItems currentItem = null;
        int noOfIngredients = 0;

        if (order==null || order.getItems()==null || order.getItems().size() < 1) {  // checking null or empty list
            throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
        }

        for (String orderItems : order.getItems()) { //outer loop: looping on all the orders
            List<String> orderItem = Arrays.asList(orderItems.split(","));
            for (String item : orderItem) {// inner loop: looping on ingredients of single order
                if (StringUtils.isBlank(item)){
                    throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
                }
                item = item.trim();

                // exclusion of ingredients without menu item
                if (item.startsWith("-") && currentItem == null) {
                    throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
                }

                // if item is not excluded
                if (!item.startsWith("-")) {
                    String itemsString = mapOfBevToCosts.get(MenuItems.getItem(item));
                    if(StringUtils.isBlank(itemsString)){
                        throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
                    }
                    List<String> listOfItemsAndPrice = Arrays.asList(itemsString.split(","));
                    BigDecimal itemCost = new BigDecimal(listOfItemsAndPrice.get(listOfItemsAndPrice.size() - 1));
                    if (itemCost != null) {
                        total = itemCost.add(total); // adding cost of menu item
                        currentItem = MenuItems.getItem(item);
                        noOfIngredients = listOfItemsAndPrice.size() - 1;
                    } else {
                        continue; // if item is not a menu item and not excluded, skip this item
                    }
                } else { // item is excluded
                    String formattedItemString = item.substring(1);
                    checkIngredientExistInItem(formattedItemString, currentItem.toString());
                    noOfIngredients--;
                    BigDecimal ingredientCost = mapOfIngredientsToCosts
                            .get(MenuItemIngredients.getItem(formattedItemString));
                    if (ingredientCost != null) {
                        total = total.subtract(ingredientCost);
                    }
                }
                if (noOfIngredients == 0) {
                    throw new InvalidOrderRequestException(Errors.EXCLUDED_ALL_INGREDIENTS, HttpStatus.BAD_REQUEST);
                }
            }

            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                // total bill is negative
                throw new InvalidOrderRequestException(Errors.TOTAL_BILL_NEGATIVE, HttpStatus.BAD_REQUEST);
            }
        }

        if(currentItem==null){
            // No menu items in order
            throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
        }


        return total;
    }

    /*
     *  Check if ingredient exists in an Item
     */
    private void checkIngredientExistInItem(String formattedItemString, String currentItem) throws InvalidOrderRequestException {
        String itemString = mapOfBevToCosts.get(MenuItems.getItem(currentItem));
        if(StringUtils.isNotBlank(itemString)) {
            List<String> listOfIngredients = Arrays.asList(itemString.split(","));
            Boolean exists = listOfIngredients.stream().anyMatch(in -> in.equalsIgnoreCase(formattedItemString));
            if(!exists){
                // Item/ingredient mismatch
                throw new InvalidOrderRequestException(Errors.ITEM_INGREDIENT_MISMATCH, HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new InvalidOrderRequestException(Errors.ITEM_INGREDIENT_MISMATCH, HttpStatus.BAD_REQUEST);
        }

    }
}
