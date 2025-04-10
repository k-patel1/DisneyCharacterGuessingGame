package com.example.disneycharacterguessinggame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    private TextView attemptsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RadioGroup themeGroup = findViewById(R.id.themeGroup);
        SeekBar attemptsBar = findViewById(R.id.attemptsBar);
        attemptsLabel = findViewById(R.id.attemptsLabel);


        int currentTheme = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            themeGroup.check(R.id.radioDark);
        } else {
            themeGroup.check(R.id.radioLight);
        }

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int theme = checkedId == R.id.radioDark ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            prefs.edit().putInt("theme", theme).apply();
            AppCompatDelegate.setDefaultNightMode(theme);
        });


        int attempts = prefs.getInt("maxAttempts", 1);
        attemptsBar.setMax(2);
        attemptsBar.setProgress(attempts - 1);
        updateAttemptsLabel(attempts);

        attemptsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1;
                updateAttemptsLabel(value);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save button
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            int attemptsValue = attemptsBar.getProgress() + 1;
            prefs.edit().putInt("maxAttempts", attemptsValue).apply();
            finish();
        });

        // Home button
        findViewById(R.id.btnHome).setOnClickListener(v -> finish());
    }

    private void updateAttemptsLabel(int attempts) {
        attemptsLabel.setText("Number of Attempts: " + attempts);
    }
}