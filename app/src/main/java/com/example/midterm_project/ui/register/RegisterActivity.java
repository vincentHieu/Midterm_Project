package com.example.midterm_project.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.R;
import com.example.midterm_project.data.api.ApiService;
import com.example.midterm_project.data.api.GatewayApiClient;
import com.example.midterm_project.data.repository.AppRepository;
import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private TextInputEditText etFullName, etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Slide-in animation when activity opens
        View root = findViewById(android.R.id.content);
        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

        TokenManager tokenManager = new TokenManager(getApplicationContext());
        ApiService apiService = GatewayApiClient.getInstance(tokenManager).create(ApiService.class);
        AppRepository repository = new AppRepository(apiService);
        RegisterViewModelFactory factory = new RegisterViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        initViews();
        observeViewModel();

        btnRegister.setOnClickListener(v -> {
            String fullName  = getText(etFullName);
            String username  = getText(etUsername);
            String email     = getText(etEmail);
            String password  = getText(etPassword);
            String confirm   = getText(etConfirmPassword);
            viewModel.register(fullName, username, email, password, confirm);
        });

        // Navigate to Login
        tvGoToLogin.setOnClickListener(v -> {
            finish(); // back to LoginActivity
        });
    }

    private void initViews() {
        etFullName        = findViewById(R.id.etFullName);
        etUsername        = findViewById(R.id.etUsername);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        progressBar       = findViewById(R.id.progressBar);
        tvGoToLogin       = findViewById(R.id.tvGoToLogin);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(this, uiState -> {
            if (uiState == null) return;
            switch (uiState.state) {
                case IDLE:
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    break;
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "✅ " + uiState.message + " Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                    // Back to Login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "❌ " + uiState.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
