package com.example.midterm_project.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.data.repository.AppRepository;

public class ProductViewModelFactory implements ViewModelProvider.Factory {

    private final AppRepository repository;

    public ProductViewModelFactory(AppRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
