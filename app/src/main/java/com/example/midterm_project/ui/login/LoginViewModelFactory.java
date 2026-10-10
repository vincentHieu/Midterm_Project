package com.example.midterm_project.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.data.repository.AppRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private final AppRepository repository;
    private final TokenManager tokenManager;

    public LoginViewModelFactory(AppRepository repository, TokenManager tokenManager) {
        this.repository = repository;
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(repository, tokenManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
