package com.example.midterm_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.ui.home.HomeFragment;
import com.example.midterm_project.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TokenManager tokenManager = new TokenManager(getApplicationContext());

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog(tokenManager));

        // Navigation Component tự động load HomeFragment thông qua NavHostFragment
    }

    private void showLogoutDialog(TokenManager tokenManager) {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    tokenManager.clearToken();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
