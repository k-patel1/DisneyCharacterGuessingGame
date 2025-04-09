package com.example.disneycharacterguessinggame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private ImageView characterImage;
    private RadioGroup optionsGroup;
    private TextView questionText, scoreText;
    private Button btnNext, btnPrevious;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean[] answered;
    private String[] userAnswers;
    private String[] correctAnswers;
    private int maxAttempts = 1;
    private int[] attemptsLeft;
    private static final int TOTAL_QUESTIONS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        maxAttempts = prefs.getInt("maxAttempts", 1);

        characterImage = findViewById(R.id.characterImage);
        optionsGroup = findViewById(R.id.optionsGroup);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        loadQuestions();
        answered = new boolean[TOTAL_QUESTIONS];
        userAnswers = new String[TOTAL_QUESTIONS];
        correctAnswers = new String[TOTAL_QUESTIONS];
        attemptsLeft = new int[TOTAL_QUESTIONS];

        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            correctAnswers[i] = questions.get(i).getCorrectAnswer();
            attemptsLeft[i] = maxAttempts;
        }

        applyThemeColors();
        updateScore();
        showQuestion(currentQuestionIndex);

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (answered[currentQuestionIndex] || checkedId == -1) return;

            RadioButton selected = findViewById(checkedId);
            String selectedAnswer = selected.getText().toString();
            String correctAnswer = correctAnswers[currentQuestionIndex];
            boolean isCorrect = selectedAnswer.equals(correctAnswer);

            userAnswers[currentQuestionIndex] = selectedAnswer;
            attemptsLeft[currentQuestionIndex]--;

            highlightAnswers(correctAnswer, selectedAnswer, isCorrect);

            if (isCorrect) {
                score++;
                answered[currentQuestionIndex] = true;
                updateScore();
            } else if (attemptsLeft[currentQuestionIndex] <= 0) {
                answered[currentQuestionIndex] = true;
            }

            if (answered[currentQuestionIndex]) {
                lockAnswerSelection();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < TOTAL_QUESTIONS - 1) {
                currentQuestionIndex++;
                showQuestion(currentQuestionIndex);
            } else {
                showResults();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                showQuestion(currentQuestionIndex);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void highlightAnswers(String correctAnswer, String selectedAnswer, boolean isCorrect) {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            String option = rb.getText().toString();

            if (option.equals(correctAnswer)) {
                rb.setTextColor(Color.GREEN);
            } else if (!isCorrect && option.equals(selectedAnswer)) {
                rb.setTextColor(Color.RED);
            }
        }
    }

    private void applyThemeColors() {
        int backgroundColor = ContextCompat.getColor(this, R.color.background_primary);
        int textColor = ContextCompat.getColor(this, R.color.text_primary);

        findViewById(R.id.gameLayout).setBackgroundColor(backgroundColor);
        questionText.setTextColor(textColor);
        scoreText.setTextColor(textColor);
    }

    private void showQuestion(int index) {
        Question question = questions.get(index);
        characterImage.setImageResource(question.getImageRes());
        questionText.setText(getString(R.string.question_format, index + 1, TOTAL_QUESTIONS));

        if (!answered[index]) {
            questionText.append("\nAttempts left: " + attemptsLeft[index]);
        }

        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();

        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());
        Collections.addAll(options, question.getWrongOptions());
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setTextSize(18);
            rb.setPadding(30, 30, 30, 30);
            rb.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            rb.setEnabled(!answered[index]);

            if (answered[index]) {
                if (option.equals(correctAnswers[index])) {
                    rb.setTextColor(Color.GREEN);
                } else if (option.equals(userAnswers[index])) {
                    rb.setTextColor(Color.RED);
                }
            }

            optionsGroup.addView(rb);
        }
    }

    private void loadQuestions() {
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.add(new Question(R.drawable.bambi, "Bambi", new String[]{"Simba", "Dumbo", "Pluto"}));
        allQuestions.add(new Question(R.drawable.mickey, "Mickey Mouse", new String[]{"Donald Duck", "Goofy", "Pluto"}));
        allQuestions.add(new Question(R.drawable.minnie, "Minnie Mouse", new String[]{"Mickey", "Donald", "Goofy"}));
        allQuestions.add(new Question(R.drawable.donald, "Donald Duck", new String[]{"Mickey", "Goofy", "Pluto"}));
        allQuestions.add(new Question(R.drawable.goofy, "Goofy", new String[]{"Mickey", "Donald", "Pluto"}));
        allQuestions.add(new Question(R.drawable.pluto, "Pluto", new String[]{"Mickey", "Donald", "Goofy"}));
        allQuestions.add(new Question(R.drawable.dumbo, "Dumbo", new String[]{"Bambi", "Simba", "Pluto"}));
        allQuestions.add(new Question(R.drawable.simba, "Simba", new String[]{"Bambi", "Dumbo", "Pluto"}));
        allQuestions.add(new Question(R.drawable.stitch, "Stitch", new String[]{"Mickey", "Donald", "Goofy"}));
        allQuestions.add(new Question(R.drawable.tinkerbell, "Tinker Bell", new String[]{"Minnie", "Daisy", "Pluto"}));
        allQuestions.add(new Question(R.drawable.olaf, "Olaf", new String[]{"Mickey", "Goofy", "Pluto"}));

        Collections.shuffle(allQuestions);
        questions = allQuestions.subList(0, TOTAL_QUESTIONS);
    }

    private void updateScore() {
        scoreText.setText(getString(R.string.score_format, score, TOTAL_QUESTIONS));
    }

    private void lockAnswerSelection() {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", TOTAL_QUESTIONS);
        startActivity(intent);
        finish();
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

        public int getImageRes() { return imageRes; }
        public String getCorrectAnswer() { return correctAnswer; }
        public String[] getWrongOptions() { return wrongOptions; }
    }
}