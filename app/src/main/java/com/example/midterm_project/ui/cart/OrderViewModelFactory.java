package com.example.midterm_project.ui.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.data.repository.AppRepository;

public class OrderViewModelFactory implements ViewModelProvider.Factory {

    private final AppRepository repository;

    public OrderViewModelFactory(AppRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
