package com.adminpanel;

import java.time.LocalDateTime;

public class Product {
    private static int idCounter = 1;
    
    private int id;
    private String name;
    private String description;
    private String image;
    private double price;
    private int categoryId;
    private LocalDateTime createAt;

    public Product(String name, String description, String image, double price, int categoryId) {
        this.id = idCounter++;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.categoryId = categoryId;
        this.createAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }
}
