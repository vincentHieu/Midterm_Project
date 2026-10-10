package com.example.midterm_project.data.model;
import java.util.List;
public class OrderRequest {
    private List<OrderItem> items;
    private String address;

    public OrderRequest(List<OrderItem> items, String address) {
        this.items = items;
        this.address = address;
    }
    public List<OrderItem> getItems() { return items; }
    public String getAddress() { return address; }
}
