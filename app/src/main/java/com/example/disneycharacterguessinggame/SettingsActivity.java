package com.example.disneycharacterguessinggame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        Button btnHome = findViewById(R.id.btnHome);
        RadioGroup themeGroup = findViewById(R.id.themeGroup);

        // Set current theme selection
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            themeGroup.check(R.id.radioLight);
        } else {
            themeGroup.check(R.id.radioDark);
        }

        // Home button click listener
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Theme selection listener
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            try {
                if (checkedId == R.id.radioLight) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.radioDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                // Delay recreation to prevent crashes
                getWindow().getDecorView().postDelayed(() -> recreate(), 100);
            } catch (Exception e) {
                Toast.makeText(this, "Error changing theme", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}