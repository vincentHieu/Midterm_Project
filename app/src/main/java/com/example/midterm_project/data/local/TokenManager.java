package com.example.midterm_project.data.local;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
public class TokenManager {
    private SharedPreferences sharedPrefs;
    public TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPrefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void saveToken(String token) {
        if (sharedPrefs != null) sharedPrefs.edit().putString("JWT_TOKEN", token).apply();
    }
    public String getToken() {
        return (sharedPrefs != null) ? sharedPrefs.getString("JWT_TOKEN", null) : null;
    }
    public void clearToken() {
        if (sharedPrefs != null) sharedPrefs.edit().remove("JWT_TOKEN").apply();
    }
}
