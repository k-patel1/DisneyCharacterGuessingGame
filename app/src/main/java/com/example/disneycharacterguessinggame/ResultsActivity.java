package com.example.disneycharacterguessinggame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        TextView resultsText = findViewById(R.id.resultsText);
        resultsText.setText(getString(R.string.results_format, score, total));


        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnMainMenu = findViewById(R.id.btnMainMenu);

        btnPlayAgain.setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });

        btnMainMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}