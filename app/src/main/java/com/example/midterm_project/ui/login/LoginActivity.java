package com.example.midterm_project.ui.login;

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

import com.example.midterm_project.MainActivity;
import com.example.midterm_project.R;
import com.example.midterm_project.data.api.ApiService;
import com.example.midterm_project.data.api.GatewayApiClient;
import com.example.midterm_project.data.repository.AppRepository;
import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.ui.register.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Fade-in animation on open
        View root = findViewById(android.R.id.content);
        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        TokenManager tokenManager = new TokenManager(getApplicationContext());
        ApiService apiService = GatewayApiClient.getInstance(tokenManager).create(ApiService.class);
        AppRepository repository = new AppRepository(apiService);

        LoginViewModelFactory factory = new LoginViewModelFactory(repository, tokenManager);
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Auto-navigate if already logged in
        if (tokenManager.getToken() != null) {
            navigateToHome();
            return;
        }

        initViews();
        observeViewModel();

        btnLogin.setOnClickListener(v -> {
            String username = getText(etUsername);
            String password = getText(etPassword);
            viewModel.login(username, password);
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void initViews() {
        etUsername     = findViewById(R.id.etUsername);
        etPassword     = findViewById(R.id.etPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        progressBar    = findViewById(R.id.progressBar);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(this, uiState -> {
            if (uiState == null) return;
            switch (uiState.state) {
                case IDLE:
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    break;
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    navigateToHome();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "❌ " + uiState.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
