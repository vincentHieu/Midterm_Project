package com.example.midterm_project.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.repository.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

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

    private final MutableLiveData<Resource<List<Product>>> _products = new MutableLiveData<>();
    public LiveData<Resource<List<Product>>> getProducts() { return _products; }

    public ProductViewModel(AppRepository repository) {
        this.repository = repository;
    }

    public void fetchProducts() {
        _products.setValue(Resource.loading());

        repository.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(Resource.success(response.body()));
                } else {
                    _products.setValue(Resource.error("Không thể tải sản phẩm. Mã lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _products.setValue(Resource.error("Lỗi kết nối: " + t.getMessage()));
            }
        });
    }
}
