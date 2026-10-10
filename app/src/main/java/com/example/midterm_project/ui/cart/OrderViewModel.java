package com.example.midterm_project.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midterm_project.data.model.OrderItem;
import com.example.midterm_project.data.model.OrderRequest;
import com.example.midterm_project.data.model.OrderResponse;
import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.repository.AppRepository;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {

    public enum Status { LOADING, SUCCESS, ERROR }

    public static class Resource<T> {
        public final Status status;
        public final T data;
        public final String message;

        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Resource<T> loading() { return new Resource<>(Status.LOADING, null, null); }
        public static <T> Resource<T> success(T data) { return new Resource<>(Status.SUCCESS, data, null); }
        public static <T> Resource<T> error(String msg) { return new Resource<>(Status.ERROR, null, msg); }
    }

    private final AppRepository repository;

    private final MutableLiveData<Resource<OrderResponse>> _orderResult = new MutableLiveData<>();
    public LiveData<Resource<OrderResponse>> getOrderResult() { return _orderResult; }

    public OrderViewModel(AppRepository repository) {
        this.repository = repository;
    }

    public void placeOrder(Product product) {
        _orderResult.setValue(Resource.loading());

        OrderItem item = new OrderItem(product.getId(), product.getQuantityToOrder());
        String address = (product.getDeliveryAddress() != null && !product.getDeliveryAddress().isEmpty())
                ? product.getDeliveryAddress()
                : "TP. Hồ Chí Minh";
        OrderRequest orderRequest = new OrderRequest(
                Collections.singletonList(item),
                address
        );

        repository.createOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _orderResult.setValue(Resource.success(response.body()));
                } else {
                    _orderResult.setValue(Resource.error("Đặt hàng thất bại. Mã lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                _orderResult.setValue(Resource.error("Lỗi kết nối: " + t.getMessage()));
            }
        });
    }
}
