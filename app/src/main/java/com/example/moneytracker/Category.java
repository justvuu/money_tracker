package com.example.moneytracker;

public class Category {
    private int id;
    private int userId;
    private String categoryName;
    private int type;

    public Category(int id, int userId, String categoryName, int type){
        this.id = id;
        this.categoryName = categoryName;
        this.type = type;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return categoryName;
    }
    public int getType() {
        return type;
    }

    public int getUserId(){return userId;}
}
