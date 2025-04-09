package com.example.disneycharacterguessinggame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(R.string.about_text);

        TextView howToPlayText = findViewById(R.id.howToPlayText);
        howToPlayText.setText(R.string.how_to_play);

        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> finish());
    }
}