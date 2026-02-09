package com.adminpanel;

public class Cart {
    private static int idCounter = 1;
    
    private int id;
    private int userId;
    private String status;
    private double totalPrice;

    public Cart(int userId, String status) {
        this.id = idCounter++;
        this.userId = userId;
        this.status = status;
        this.totalPrice = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
