package com.example.practiceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView diagram;
    private Button submitButton;
    private Button nextQuestionButton;
    private Button similarQuestionButton;
    private TextView questionText;
    private TextView answerResult;
    private RadioGroup answerGroup;
    private RadioButton[] answerButtonList = new RadioButton[5];
    private int correctAnswerIndex;
    private boolean isSecondTry;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set variables to reference UI View objects
        diagram = findViewById(R.id.questionImage);
        submitButton = findViewById(R.id.submitButton);
        nextQuestionButton = findViewById(R.id.nextButton);
        similarQuestionButton = findViewById(R.id.similarButton);
        questionText = findViewById(R.id.questionText);
        answerResult = findViewById(R.id.answerResult);
        answerGroup = findViewById(R.id.radioGroup);
        answerButtonList[0] = findViewById(R.id.answerA);
        answerButtonList[1] = findViewById(R.id.answerB);
        answerButtonList[2] = findViewById(R.id.answerC);
        answerButtonList[3] = findViewById(R.id.answerD);
        answerButtonList[4] = findViewById(R.id.answerE);
        isSecondTry = false;
        //testId = "ACTB04";
        //questionId = "M00";
        //questionIndex = Math.max(Integer.parseInt(questionId.substring(1)) - 1, 0);

        if(savedInstanceState != null) {
            if (savedInstanceState.getCharArray("QuestionCode") == null) { //No question has been saved. Generate the first question.
                currentQuestion = new Question(this);
                populateNewQuestion();
            } else {
                currentQuestion = Question.RestoreSelectQuestion(String.valueOf(savedInstanceState.getCharArray("QuestionCode")), this);
                populateRecoveredQuestion();
            }
        }
        else {
            currentQuestion = new Question(this);
            populateNewQuestion();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Submit button should:
                * 1: Check to see if the selected answer is correct.
                * 2: If correct, should display "Correct!" and give the option to move on.
                * 3: If incorrect, should display "Incorrect." and give the option to retrieve another similar question.
                 */

                //Step 1: Check to see if the selected answer is correct.
                if(answerButtonList[correctAnswerIndex].getId() == answerGroup.getCheckedRadioButtonId()) {
                    //Step 2: If correct, should display "Correct!" and give the option to move on.

                    nextQuestionButton.setClickable(true);
                    nextQuestionButton.setVisibility(View.VISIBLE);
                    similarQuestionButton.setClickable(true);
                    similarQuestionButton.setVisibility(View.VISIBLE);
                    submitButton.setClickable(false);
                    submitButton.setVisibility(View.GONE); //no point on hitting submit again.
                    answerButtonList[correctAnswerIndex].setTextColor(Color.parseColor("#00BB00"));
                    answerResult.setVisibility(View.VISIBLE);
                    answerResult.setText("Correct!");
                    answerResult.setTextColor(Color.parseColor("#00BB00"));

                }
                else {
                    //Step 3: If incorrect, should display "Incorrect." and give the option to try a different answer.
                    RadioButton checked = findViewById(answerGroup.getCheckedRadioButtonId());
                    checked.setTextColor(Color.RED);
                    int checkedWrongAnswerIndex = 0;
                    for(int i = 0; i < 5; i++){
                        //get the checked button's index
                        if(answerButtonList[i].isChecked()){
                            for(int j = 0; j < 4; j++){
                                if(answerButtonList[i].getText().toString().equals(currentQuestion.getWrongAnswers()[j])) {
                                    checkedWrongAnswerIndex = j;
                                }
                            }
                        }
                    }

                    if(!isSecondTry) {
                        //if we are still on the first try, let the student try again.
                        answerResult.setVisibility(View.VISIBLE);
                        answerResult.setTextColor(Color.RED);
                        StringBuilder outputText = new StringBuilder("Incorrect.\n");
                        outputText.append(currentQuestion.getWrongAnswerClues()[checkedWrongAnswerIndex]);
                        outputText.append("\nTry another answer...");
                        answerResult.setText(outputText.toString());
                        isSecondTry = true;
                    }
                    else {
                        //if they still get it wrong after the second try, tell them the answer is incorrect and enable the similar question button.
                        nextQuestionButton.setClickable(true);
                        nextQuestionButton.setVisibility(View.VISIBLE);
                        similarQuestionButton.setClickable(true);
                        similarQuestionButton.setVisibility(View.VISIBLE);
                        submitButton.setClickable(false);
                        submitButton.setVisibility(View.GONE);
                        answerButtonList[correctAnswerIndex].setTextColor(Color.parseColor("#00BB00"));
                        StringBuilder outputText = new StringBuilder("Incorrect.\n");
                        outputText.append(currentQuestion.getExplanation());
                        outputText.append("\nThe correct answer is marked in green. Please try again with a new question.");
                        answerResult.setText(outputText.toString());
                        isSecondTry = false;
                    }
                    //answerButtonList[correctAnswerIndex].setTextColor(Color.GREEN);
                }
            }
        });
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDisplay();
                populateNewQuestion();
            }
        });
        similarQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDisplay();
                populateSimilarQuestion();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle SavedInstanceState){
        super.onSaveInstanceState(SavedInstanceState);
        SavedInstanceState.putCharArray("QuestionCode", currentQuestion.getQuestionCode().toCharArray());
    }

    protected Context getContextForClickListener() {
        //simply return the current context for the listeners to use.
        return this;
    }

    public void resetDisplay() {
        if(submitButton.getVisibility() == View.INVISIBLE || submitButton.getVisibility() == View.GONE) { //Reset the submit button for the next question.
            submitButton.setVisibility(View.VISIBLE);
            submitButton.setClickable(true);
        }
        if(nextQuestionButton.getVisibility() == View.VISIBLE) { //Reset the next question button.
            nextQuestionButton.setVisibility(View.GONE);
            nextQuestionButton.setClickable(false);
        }
        if(similarQuestionButton.getVisibility() == View.VISIBLE) { //Reset the similar question button.
            similarQuestionButton.setVisibility(View.GONE);
            similarQuestionButton.setClickable(false);
        }
        if(answerResult.getCurrentTextColor() != Color.BLACK) {
            answerResult.setTextColor(Color.BLACK);
            answerResult.setText("");
            answerResult.setVisibility(View.GONE);
        }
        for(int i = 0; i < answerButtonList.length; i++) {
            answerButtonList[i].setTextColor(Color.BLACK);
            answerButtonList[i].setText("");
            /*if(answerButtonList[i].isChecked()) {
                answerButtonList[i].setChecked(false);
            }*/
        }
        isSecondTry = false;
    }

    /*
     * The following method is used to get a random question of a different type than the current one.
     * The method needs to change the question ID (not yet implemented), then use that question ID to pick a random question with that ID.
     */
    public void populateNewQuestion() {
        Random rand = new Random();
        correctAnswerIndex = (rand.nextInt() & Integer.MAX_VALUE) % 5;
        currentQuestion.buildNewQuestion();
        currentQuestion.shuffleWrongAnswers(10);

        if (!currentQuestion.getQuestionImageResource().equals("")) {
            diagram.setVisibility(View.VISIBLE);
            diagram.setImageResource(this.getResources().getIdentifier(currentQuestion.getQuestionImageResource(), "drawable", "com.example.practiceapp"));
        }
        else {
            diagram.setVisibility(View.GONE);
        }

        questionText.setText(Html.fromHtml(currentQuestion.getQuestionText()));
        int incorrectAnswerIndex = 0;
        for (int i = 0; i < 5; i++) {
            if (i == correctAnswerIndex) {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getCorrectAnswer()));
            }
            else {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getWrongAnswers()[incorrectAnswerIndex]));
                incorrectAnswerIndex++;
            }
        }
    }

    /*
     * The following method is used to get a random question with the same question ID as the current one.
     * This is like populateNewQuestion, but it doesn't change the question ID.
     */
    public void populateSimilarQuestion() {
        Random rand = new Random();
        correctAnswerIndex = (rand.nextInt() & Integer.MAX_VALUE) % 5;
        currentQuestion.buildSimilarQuestion();
        currentQuestion.shuffleWrongAnswers(10);

        if (!currentQuestion.getQuestionImageResource().equals("")) {
            diagram.setVisibility(View.VISIBLE);
            diagram.setImageResource(this.getResources().getIdentifier(currentQuestion.getQuestionImageResource(), "drawable", "com.example.practiceapp"));
        }
        else {
            diagram.setVisibility(View.GONE);
        }

        questionText.setText(Html.fromHtml(currentQuestion.getQuestionText()));
        int incorrectAnswerIndex = 0;
        for (int i = 0; i < 5; i++) {
            if (i == correctAnswerIndex) {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getCorrectAnswer()));
            }
            else {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getWrongAnswers()[incorrectAnswerIndex]));
                incorrectAnswerIndex++;
            }
        }
    }

    public void populateRecoveredQuestion() {
        currentQuestion.shuffleWrongAnswers(10);

        if (!currentQuestion.getQuestionImageResource().equals("")) {
            diagram.setVisibility(View.VISIBLE);
            diagram.setImageResource(this.getResources().getIdentifier(currentQuestion.getQuestionImageResource(), "drawable", "com.example.practiceapp"));
        }
        else {
            diagram.setVisibility(View.GONE);
        }

        questionText.setText(Html.fromHtml(currentQuestion.getQuestionText()));
        int incorrectAnswerIndex = 0;
        for (int i = 0; i < 5; i++) {
            if (i == correctAnswerIndex) {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getCorrectAnswer()));
            }
            else {
                answerButtonList[i].setText(Html.fromHtml(currentQuestion.getWrongAnswers()[incorrectAnswerIndex]));
                incorrectAnswerIndex++;
            }
        }
    }
}
