package com.example.midterm_project.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.data.model.LoginRequest;
import com.example.midterm_project.data.model.LoginResponse;
import com.example.midterm_project.data.repository.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    public enum State { IDLE, LOADING, SUCCESS, ERROR }

    public static class UiState {
        public final State state;
        public final String message;
        public UiState(State state, String message) {
            this.state = state;
            this.message = message;
        }
    }

    private final AppRepository repository;
    private final TokenManager tokenManager;
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(new UiState(State.IDLE, ""));

    public LoginViewModel(AppRepository repository, TokenManager tokenManager) {
        this.repository   = repository;
        this.tokenManager = tokenManager;
    }

    public LiveData<UiState> getUiState() { return _uiState; }

    public void login(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            _uiState.setValue(new UiState(State.ERROR, "Vui lòng nhập đầy đủ thông tin"));
            return;
        }

        _uiState.setValue(new UiState(State.LOADING, ""));

        LoginRequest request = new LoginRequest(username, password);
        repository.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    _uiState.setValue(new UiState(State.SUCCESS, ""));
                } else {
                    _uiState.setValue(new UiState(State.ERROR, "Đăng nhập thất bại. Sai tài khoản hoặc mật khẩu."));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                _uiState.setValue(new UiState(State.ERROR, "Lỗi kết nối: " + t.getMessage()));
            }
        });
    }
}
