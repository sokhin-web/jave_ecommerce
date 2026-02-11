package com.adminpanel.model;

public class CartItem {
    private static int idCounter = 1;
    
    private int id;
    private int cartId;
    private int productId;
    private double price;
    private int quantity;

    public CartItem(int cartId, int productId, double price, int quantity) {
        this.id = idCounter++;
        this.cartId = cartId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getTotal() { return price * quantity; }
}
