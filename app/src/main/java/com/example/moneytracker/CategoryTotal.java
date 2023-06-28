package com.example.moneytracker;

public class CategoryTotal {
    private String categoryName;
    private double totalAmount;

    public CategoryTotal(String categoryName, double totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
