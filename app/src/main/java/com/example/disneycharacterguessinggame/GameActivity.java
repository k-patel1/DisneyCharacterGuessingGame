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
import androidx.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private ImageView characterImage;
    private RadioGroup optionsGroup;
    private TextView questionText, scoreText, attemptsText;
    private Button btnNext, btnPrevious;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean[] questionAnswered;
    private String[] userAnswers;
    private String[] correctAnswers;
    private int[] attemptsLeft;
    private static final int TOTAL_QUESTIONS = 5;
    private int baseAttempts = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        loadSettings();
        initializeNewGame();
        setupListeners();
        applyThemeColors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        if (questions != null) {
            showQuestion(currentQuestionIndex);
        }
    }

    private void initializeViews() {
        characterImage = findViewById(R.id.characterImage);
        optionsGroup = findViewById(R.id.optionsGroup);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        attemptsText = findViewById(R.id.attemptsText);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
    }

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        baseAttempts = prefs.getInt("maxAttempts", 1);

        if (attemptsLeft == null || attemptsLeft.length != TOTAL_QUESTIONS) {
            attemptsLeft = new int[TOTAL_QUESTIONS];
        }

        // First question gets baseAttempts, others get baseAttempts+1
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            if (userAnswers == null || userAnswers[i] == null) {
                attemptsLeft[i] = (i == 0) ? baseAttempts : baseAttempts + 1;
            }
        }
    }

    private void initializeNewGame() {
        loadQuestions();
        questionAnswered = new boolean[TOTAL_QUESTIONS];
        userAnswers = new String[TOTAL_QUESTIONS];
        correctAnswers = new String[TOTAL_QUESTIONS];
        attemptsLeft = new int[TOTAL_QUESTIONS];

        // Initialize attempts - Q1 gets baseAttempts, Q2-5 get baseAttempts+1
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            correctAnswers[i] = questions.get(i).getCorrectAnswer();
            questionAnswered[i] = false;
            userAnswers[i] = null;
            attemptsLeft[i] = (i == 0) ? baseAttempts : baseAttempts + 1;
        }

        score = 0;
        currentQuestionIndex = 0;
        updateScore();
        showQuestion(currentQuestionIndex);
    }

    private void setupListeners() {
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (questionAnswered[currentQuestionIndex] || checkedId == -1) return;

            RadioButton selected = findViewById(checkedId);
            String selectedAnswer = selected.getText().toString();
            userAnswers[currentQuestionIndex] = selectedAnswer;
            attemptsLeft[currentQuestionIndex]--;

            updateAttemptsDisplay();

            if (selectedAnswer.equals(correctAnswers[currentQuestionIndex])) {
                handleCorrectAnswer();
            } else {
                handleWrongAnswer();
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

    private void handleCorrectAnswer() {
        highlightAnswer(true);
        score++;
        questionAnswered[currentQuestionIndex] = true;
        updateScore();
        lockAnswerSelection();
    }

    private void handleWrongAnswer() {
        highlightAnswer(false);
        if (attemptsLeft[currentQuestionIndex] <= 0) {
            highlightCorrectAnswer();
            questionAnswered[currentQuestionIndex] = true;
            lockAnswerSelection();
        }
    }

    private void highlightAnswer(boolean isCorrect) {
        int color = isCorrect ? Color.GREEN : Color.RED;
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            if (rb.getText().equals(userAnswers[currentQuestionIndex])) {
                rb.setTextColor(color);
            }
        }
    }

    private void highlightCorrectAnswer() {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            if (rb.getText().equals(correctAnswers[currentQuestionIndex])) {
                rb.setTextColor(Color.GREEN);
            }
        }
    }

    private void showQuestion(int index) {
        Question question = questions.get(index);
        characterImage.setImageResource(question.getImageRes());
        questionText.setText(getString(R.string.question_format, index + 1, TOTAL_QUESTIONS));

        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();
        updateAttemptsDisplay();

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

            if (questionAnswered[index]) {
                rb.setEnabled(false);
                if (option.equals(correctAnswers[index])) {
                    rb.setTextColor(Color.GREEN);
                } else if (option.equals(userAnswers[index])) {
                    rb.setTextColor(Color.RED);
                }
            } else {
                rb.setEnabled(attemptsLeft[index] > 0);
            }

            optionsGroup.addView(rb);
        }
    }

    private void updateAttemptsDisplay() {
        attemptsText.setText("Attempts left: " + attemptsLeft[currentQuestionIndex]);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score + "/" + TOTAL_QUESTIONS);
    }

    private void lockAnswerSelection() {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(false);
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

    private void applyThemeColors() {
        int backgroundColor = ContextCompat.getColor(this, R.color.background_primary);
        int textColor = ContextCompat.getColor(this, R.color.text_primary);

        findViewById(R.id.gameLayout).setBackgroundColor(backgroundColor);
        questionText.setTextColor(textColor);
        scoreText.setTextColor(textColor);
        attemptsText.setTextColor(textColor);
    }

    private void showResults() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", TOTAL_QUESTIONS);
        startActivity(intent);
        finish();
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