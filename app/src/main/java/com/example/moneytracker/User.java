package com.example.moneytracker;

public class User {
    private long id;
    private String username;
    private String password;

    private double income;

    private double expense;


    public User() {
        // Default constructor
    }

    public User(long id, String username, String password, double income, double expense) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.income = income;
        this.expense = expense;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.income = 0;
        this.expense = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIncome(double income){this.income = income;}
    public double getIncome(){return this.income;}

    public void setExpense(double income){this.income = income;}
    public double getExpense(){return this.expense;}
}
