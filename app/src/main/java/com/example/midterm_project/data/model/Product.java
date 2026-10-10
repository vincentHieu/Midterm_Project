package com.example.midterm_project.data.model;

public class Product {
    // Server fields (from JSON)
    private String id;
    private String name;
    private long price;
    private String description;

    // Local-only fields for order form (not from server)
    private transient int quantityToOrder = 1;
    private transient String deliveryAddress = "";

    // --- Getters ---
    public String getId()          { return id; }
    public String getName()        { return name; }
    public long getPrice()         { return price; }
    public String getDescription() { return description; }
    public int getQuantityToOrder()    { return quantityToOrder; }
    public String getDeliveryAddress() { return deliveryAddress; }

    // --- Setters (for OrderDetailActivity) ---
    public void setId(String id)                       { this.id = id; }
    public void setName(String name)                   { this.name = name; }
    public void setPrice(long price)                   { this.price = price; }
    public void setDescription(String description)     { this.description = description; }
    public void setQuantityToOrder(int qty)            { this.quantityToOrder = qty; }
    public void setDeliveryAddress(String address)     { this.deliveryAddress = address; }
}
