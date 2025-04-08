package com.example.disneycharacterguessinggame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ImageView characterImage;
    private RadioGroup optionsGroup;
    private TextView questionText, scoreText;
    private Button btnNext, btnPrevious, btnHome;
    private List<Question> allQuestions;
    private List<Question> currentGameQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean[] answeredCorrectly;
    private static final int TOTAL_QUESTIONS_PER_GAME = 5; // Show 5 random questions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize views
        characterImage = findViewById(R.id.characterImage);
        optionsGroup = findViewById(R.id.optionsGroup);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnHome = findViewById(R.id.btnHome);

        // Image optimization
        characterImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        characterImage.setAdjustViewBounds(true);

        // Home button
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Load all possible questions and select random ones
        loadAllQuestions();
        selectRandomQuestions();
        answeredCorrectly = new boolean[TOTAL_QUESTIONS_PER_GAME];
        updateScore();
        showQuestion(currentQuestionIndex);

        // Navigation buttons
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < currentGameQuestions.size() - 1) {
                currentQuestionIndex++;
                showQuestion(currentQuestionIndex);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                showQuestion(currentQuestionIndex);
            }
        });

        // Answer selection
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                checkAnswer(selected.getText().toString());
            }
        });
    }

    private void loadAllQuestions() {
        allQuestions = new ArrayList<>();
        allQuestions.add(new Question(R.drawable.bambi, "Bambi", new String[]{"Simba", "Dumbo", "Pluto"}));
        allQuestions.add(new Question(R.drawable.donald, "Donald", new String[]{"Goofy", "Mickey", "Pluto"}));
        allQuestions.add(new Question(R.drawable.dumbo, "Dumbo", new String[]{"Bambi", "Simba", "Olaf"}));
        allQuestions.add(new Question(R.drawable.goofy, "Goofy", new String[]{"Donald", "Mickey", "Pluto"}));
        allQuestions.add(new Question(R.drawable.mickey, "Mickey", new String[]{"Donald", "Goofy", "Pluto"}));
        allQuestions.add(new Question(R.drawable.minnie, "Minnie", new String[]{"Tinkerbell", "Daisy", "Olaf"}));
        allQuestions.add(new Question(R.drawable.olaf, "Olaf", new String[]{"Simba", "Pluto", "Stitch"}));
        allQuestions.add(new Question(R.drawable.pluto, "Pluto", new String[]{"Goofy", "Donald", "Mickey"}));
        allQuestions.add(new Question(R.drawable.simba, "Simba", new String[]{"Bambi", "Dumbo", "Olaf"}));
        allQuestions.add(new Question(R.drawable.stitch, "Stitch", new String[]{"Pluto", "Simba", "Olaf"}));
        allQuestions.add(new Question(R.drawable.tinkerbell, "Tinkerbell", new String[]{"Minnie", "Daisy", "Olaf"}));
    }

    private void selectRandomQuestions() {
        currentGameQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(currentGameQuestions);

        // Keep only the first TOTAL_QUESTIONS_PER_GAME questions
        if (currentGameQuestions.size() > TOTAL_QUESTIONS_PER_GAME) {
            currentGameQuestions = currentGameQuestions.subList(0, TOTAL_QUESTIONS_PER_GAME);
        }
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = currentGameQuestions.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            rb.setBackgroundColor(rb.getText().equals(correctAnswer) ? Color.GREEN :
                    (rb.getText().equals(selectedAnswer) ? Color.RED : Color.TRANSPARENT));
        }

        if (selectedAnswer.equals(correctAnswer) && !answeredCorrectly[currentQuestionIndex]) {
            score++;
            answeredCorrectly[currentQuestionIndex] = true;
            updateScore();
        }
    }

    private void updateScore() {
        scoreText.setText("Score: " + score + "/" + TOTAL_QUESTIONS_PER_GAME);
    }

    private void showQuestion(int index) {
        Question question = currentGameQuestions.get(index);
        characterImage.setImageResource(question.getImageRes());
        questionText.setText("Question " + (index + 1) + " of " + TOTAL_QUESTIONS_PER_GAME);
        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();

        // Add shuffled options
        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());
        Collections.addAll(options, question.getWrongOptions());
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setTextSize(18);
            rb.setPadding(30, 30, 30, 30);
            optionsGroup.addView(rb);
        }
    }

    private static class Question {
        private final int imageRes;
        private final String correctAnswer;
        private final String[] wrongOptions;

        public Question(int imageRes, String correctAnswer, String[] wrongOptions) {
            this.imageRes = imageRes;
            this.correctAnswer = correctAnswer;
            this.wrongOptions = wrongOptions;
        }

        public int getImageRes() {
            return imageRes;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String[] getWrongOptions() {
            return wrongOptions;
        }
    }
}