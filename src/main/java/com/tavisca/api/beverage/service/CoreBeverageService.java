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

    private BigDecimal total = BigDecimal.ZERO;
    private MenuItems currentItem = null;
    private int noOfIngredients = 0; // for checking if all ingredients are excluded

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
        if (StringUtils.isBlank(order.getItems())) {
            throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
        }

        List<String> orderItems = Arrays.asList(order.getItems().split(","));
        for (String item : orderItems) {

            item = item.trim();

            // exclusion of ingredients without menu item
            if(item.startsWith("-") && this.currentItem==null){
                throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
            }

            // if item is not excluded
            if (!item.startsWith("-")) {
                String itemsString = mapOfBevToCosts.get(MenuItems.getItem(item));
                List<String> listOfItemsAndPrice = Arrays.asList(itemsString.split(","));
                BigDecimal itemCost = new BigDecimal(listOfItemsAndPrice.get(listOfItemsAndPrice.size()-1));
                if (itemCost != null) {
                    total = itemCost.add(total); // adding cost of menu item
                    this.currentItem = MenuItems.getItem(item);
                    this.noOfIngredients = listOfItemsAndPrice.size()-1;
                } else {
                    continue; // if item is not a menu item and not excluded, skip this item
                }
            } else { // item is excluded
                String formattedItemString  = item.substring(1);
                checkIngredientExistInItem(formattedItemString);
                noOfIngredients--;
                BigDecimal ingredientCost = mapOfIngredientsToCosts
                        .get(MenuItemIngredients.getItem(formattedItemString));
                if(ingredientCost!=null){
                    total = total.subtract(ingredientCost);
                }
            }
            if (noOfIngredients==0){
                throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
            }
        }

        if(total.compareTo(BigDecimal.ZERO) <= 0){
            // total bill is negative
            throw new InvalidOrderRequestException(Errors.TOTAL_BILL_NEGATIVE, HttpStatus.BAD_REQUEST);
        }

        if(currentItem==null){
            // No menu items in order
            throw new InvalidOrderRequestException(Errors.INVALID_ORDER_REQUEST, HttpStatus.BAD_REQUEST);
        }


        return this.total;
    }

    /*
     *  Check if ingredient exists in an Item
     */
    private void checkIngredientExistInItem(String formattedItemString) throws InvalidOrderRequestException {
        String itemString = mapOfBevToCosts.get(currentItem);
        List<String> listOfIngredients = Arrays.asList(itemString.split(","));
        Boolean exists = listOfIngredients.stream().anyMatch(in->in.equalsIgnoreCase(formattedItemString));
        if(!exists){
            // Item/ingredient mismatch
            throw new InvalidOrderRequestException(Errors.ITEM_INGREDIENT_MISMATCH, HttpStatus.BAD_REQUEST);
        }
    }
}
