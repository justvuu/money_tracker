package com.example.moneytracker;

public class Transaction {
    private int id;
    private double amount;
    private int categoryId;
    private String date;
    private String description;
    private int userId;

    public Transaction() {
    }
    public Transaction(int id, double amount, int categoryId, String date, String description, int userId) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
        this.description = description;
        this.userId = userId;
    }

    // Getters and setters for the attributes
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
