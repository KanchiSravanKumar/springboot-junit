package com.tavisca.api.beverage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuItems {
    COFFEE("coffee"),
    CHAI("chai"),
    TEA("tea"),
    BANANA_SMOOTHIE("Banana Smoothie"),
    STRAWBERRY_SHAKE("Strawberry Shake"),
    MOJITO("Mojito");

    private final String item;

    public static MenuItems getItem(String beverage) {
        for (MenuItems menuItem : MenuItems.values()) {
            if (menuItem.item.equalsIgnoreCase(beverage)) {
                return menuItem;
            }
        }
        return null;
    }
}
