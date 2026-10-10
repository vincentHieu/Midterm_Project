package com.example.midterm_project.data.repository;

import com.example.midterm_project.data.api.ApiService;
import com.example.midterm_project.data.model.LoginRequest;
import com.example.midterm_project.data.model.LoginResponse;
import com.example.midterm_project.data.model.OrderRequest;
import com.example.midterm_project.data.model.OrderResponse;
import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.model.RegisterRequest;

import java.util.List;

import retrofit2.Call;

public class AppRepository {
    private final ApiService apiService;

    public AppRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return apiService.login(request);
    }

    public Call<LoginResponse> register(RegisterRequest request) {
        return apiService.register(request);
    }

    public Call<List<Product>> getProducts() {
        return apiService.getProducts();
    }

    public Call<OrderResponse> createOrder(OrderRequest request) {
        return apiService.createOrder(request);
    }
}
