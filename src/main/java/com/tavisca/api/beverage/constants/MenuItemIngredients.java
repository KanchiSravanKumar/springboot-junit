package com.tavisca.api.beverage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuItemIngredients {
    MILK("milk"),
    SUGAR("sugar"),
    SODA("soda"),
    MINT("mint"),
    WATER("water");

    private final String item;

    public static MenuItemIngredients getItem(String beverage) {
        for (MenuItemIngredients menuItem : MenuItemIngredients.values()) {
            if (menuItem.item.equalsIgnoreCase(beverage)) {
                return menuItem;
            }
        }
        return null;
    }
}
