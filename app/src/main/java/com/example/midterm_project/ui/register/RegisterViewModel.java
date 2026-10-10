package com.example.midterm_project.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midterm_project.data.model.LoginResponse;
import com.example.midterm_project.data.model.RegisterRequest;
import com.example.midterm_project.data.repository.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

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
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(new UiState(State.IDLE, ""));
    public LiveData<UiState> getUiState() { return _uiState; }

    public RegisterViewModel(AppRepository repository) {
        this.repository = repository;
    }

    public void register(String fullName, String username, String email,
                         String password, String confirmPassword) {
        if (fullName.trim().isEmpty() || username.trim().isEmpty()
                || email.trim().isEmpty() || password.trim().isEmpty()) {
            _uiState.setValue(new UiState(State.ERROR, "Vui lòng điền đầy đủ thông tin"));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.setValue(new UiState(State.ERROR, "Email không hợp lệ"));
            return;
        }
        if (password.length() < 6) {
            _uiState.setValue(new UiState(State.ERROR, "Mật khẩu phải có ít nhất 6 ký tự"));
            return;
        }
        if (!password.equals(confirmPassword)) {
            _uiState.setValue(new UiState(State.ERROR, "Mật khẩu xác nhận không khớp"));
            return;
        }

        _uiState.setValue(new UiState(State.LOADING, ""));

        RegisterRequest request = new RegisterRequest(fullName, username, email, password);
        repository.register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    _uiState.setValue(new UiState(State.SUCCESS, "Đăng ký thành công!"));
                } else {
                    _uiState.setValue(new UiState(State.ERROR, "Đăng ký thất bại. Tên đăng nhập đã tồn tại."));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                _uiState.setValue(new UiState(State.ERROR, "Lỗi kết nối: " + t.getMessage()));
            }
        });
    }
}
