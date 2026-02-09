package com.adminpanel;

public class Category {
    private static int idCounter = 1;
    
    private int id;
    private String name;
    private String description;
    private String icon;

    public Category(String name, String description, String icon) {
        this.id = idCounter++;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
