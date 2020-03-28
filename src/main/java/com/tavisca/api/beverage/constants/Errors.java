package com.tavisca.api.beverage.constants;

public enum Errors {

    INVALID_ORDER_REQUEST("Invalid order request."),
    ITEM_INGREDIENT_MISMATCH("Item/ingredient mismatch."),
    TOTAL_BILL_NEGATIVE("Total bill cannot be negative."),
    INVALID_INGRIDIENT("Invalid ingredient.");


    private String error;
    Errors(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return this.error;
    }
}
