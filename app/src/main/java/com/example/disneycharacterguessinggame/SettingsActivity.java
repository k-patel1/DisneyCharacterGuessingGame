package com.example.disneycharacterguessinggame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RadioGroup themeGroup = findViewById(R.id.themeGroup);
        SeekBar attemptsBar = findViewById(R.id.attemptsBar);
        TextView attemptsText = findViewById(R.id.attemptsText);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnHome = findViewById(R.id.btnHome);

        // Theme selection
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        ((RadioButton)findViewById(nightMode == AppCompatDelegate.MODE_NIGHT_YES
                ? R.id.radioDark : R.id.radioLight)).setChecked(true);

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean darkMode = checkedId == R.id.radioDark;
            prefs.edit().putBoolean("dark_mode", darkMode).apply();
            AppCompatDelegate.setDefaultNightMode(
                    darkMode ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Attempts selection
        int attempts = prefs.getInt("maxAttempts", 1);
        attemptsBar.setProgress(attempts - 1);
        attemptsText.setText("Attempts per question: " + attempts);

        attemptsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1;
                attemptsText.setText("Attempts per question: " + value);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> {
            int attemptsValue = attemptsBar.getProgress() + 1;
            prefs.edit().putInt("maxAttempts", attemptsValue).apply();
            finish();
        });

        btnHome.setOnClickListener(v -> finish());
    }
}