package com.example.midterm_project.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.data.repository.AppRepository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    private final AppRepository repository;

    public RegisterViewModelFactory(AppRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
