package com.example.midterm_project.data.api;

import com.example.midterm_project.data.model.LoginRequest;
import com.example.midterm_project.data.model.LoginResponse;
import com.example.midterm_project.data.model.OrderRequest;
import com.example.midterm_project.data.model.OrderResponse;
import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.model.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // ──────────────── AUTH SERVICE (qua API Gateway) ────────────────
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    // ──────────────── PRODUCT SERVICE (qua API Gateway) ────────────────
    @GET("api/v1/products")
    Call<List<Product>> getProducts();

    @GET("api/v1/products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    // ──────────────── ORDER SERVICE (qua API Gateway) ────────────────
    @POST("api/v1/orders")
    Call<OrderResponse> createOrder(@Body OrderRequest request);
}
