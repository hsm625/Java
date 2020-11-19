package com.example.practiceapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Question {

    private int questionVersionNumber;
    private JSONFileHandler jsonFileHandler;
    private String testId;
    private String questionCode;
    private String questionId;
    private String versionId;
    private String questionText;
    private String correctAnswer;
    private String explanation;
    private int difficulty;
    private String[] wrongAnswers = new String[4];
    private String[] wrongAnswerClues = new String[4];
    private JSONObject JSONFromFile;
    private int prevQuestionVersionNumber = -1;
    private String questionImageResource;

    public Question(Context context){
        questionVersionNumber = 1;
        testId = "ACTB04";
        //questionCode = "ACTB04M010";
        questionId = "M00";
        versionId = "0";
        questionCode = testId + questionId + versionId;
        questionText = "This is a test. The questionText to be answered will go here.";
        correctAnswer = "Sounds good.";
        difficulty = 50;
        wrongAnswers[0] = "This isn't a question!";
        wrongAnswers[1] = "This statement is a lie.";
        wrongAnswers[2] = "Give me a real question!";
        wrongAnswers[3] = "I'm running out of wrong answers to write...";
        jsonFileHandler = new JSONFileHandler(context, "ACTQuestionList.json");
        try {
            JSONFromFile = new JSONObject(jsonFileHandler.loadJSONFromAsset());
        } catch (JSONException jse) {
            jse.printStackTrace();
            return;
        }
    }



    public String getQuestionCode() {
        return questionCode;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String[] getWrongAnswers() {
        return wrongAnswers;
    }

    public String[] getWrongAnswerClues() {
        return wrongAnswerClues;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getQuestionImageResource() { return questionImageResource; }



    public static Question RestoreSelectQuestion(String QuestionCode, Context context) {
        Question restoredQuestion = new Question(context);
        restoredQuestion.testId = QuestionCode.substring(0, 6);
        restoredQuestion.questionCode = QuestionCode;
        restoredQuestion.questionId = QuestionCode.substring(6, 9);
        restoredQuestion.versionId = QuestionCode.substring(9);
        restoredQuestion.questionVersionNumber = Integer.parseInt(restoredQuestion.versionId);
        restoredQuestion.jsonFileHandler = new JSONFileHandler(context, "ACTQuestionList.json");
        try {
            restoredQuestion.JSONFromFile = new JSONObject(restoredQuestion.jsonFileHandler.loadJSONFromAsset());
        } catch (JSONException jse) {
            jse.printStackTrace();
            restoredQuestion.questionText = "There was an error encountered when loading the JSON string.";
            return restoredQuestion;
        }

        String jsonString = restoredQuestion.jsonFileHandler.loadJSONFromAsset();
        try {
            restoredQuestion.JSONFromFile = new JSONObject(jsonString);
            JSONArray currentTest = restoredQuestion.JSONFromFile.getJSONArray(restoredQuestion.testId);
            JSONObject currentQuestionJsonObject = currentTest.getJSONObject(Integer.parseInt(restoredQuestion.questionId.substring(1)) - 1);
            JSONArray currentQuestion = currentQuestionJsonObject.getJSONArray(restoredQuestion.questionId);
            JSONObject currentQuestionVersion = currentQuestion.getJSONObject(restoredQuestion.questionVersionNumber - 1);
            JSONArray wrongAnswerJsonArray = currentQuestionVersion.getJSONArray("WrongAnswers");

            restoredQuestion.questionImageResource = currentQuestionVersion.getString("QuestionImage");
            restoredQuestion.questionText = currentQuestionVersion.getString("QuestionText");
            restoredQuestion.correctAnswer = currentQuestionVersion.getString("CorrectAnswer");
            restoredQuestion.explanation = currentQuestionVersion.getString("Explanation");
            restoredQuestion.difficulty = currentQuestionVersion.getInt("Difficulty");
            for (int i = 0; i < wrongAnswerJsonArray.length(); i++) {
                restoredQuestion.wrongAnswers[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswer");
                restoredQuestion.wrongAnswerClues[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswerClue");
            }
            return restoredQuestion;
        } catch (JSONException jse){
            jse.printStackTrace();
            return restoredQuestion;
        }
    }

    public void buildSimilarQuestion() {
        Random rand = new Random();
        //this.questionVersionNumber = questionVersionN;
        //this.questionCode = String.format("%s%s%d", testId, questionId, questionVersionNumber);
        //after these first two items are generated, grab the rest of the data from wherever it is stored.
        String jsonString = jsonFileHandler.loadJSONFromAsset();
        try {
            JSONFromFile = new JSONObject(jsonString);
            JSONArray currentTest = JSONFromFile.getJSONArray(this.testId);
            JSONObject currentQuestionJsonObject = currentTest.getJSONObject(Integer.parseInt(this.questionId.substring(1)) - 1);
            JSONArray currentQuestion = currentQuestionJsonObject.getJSONArray(this.questionId);
            this.questionVersionNumber = this.prevQuestionVersionNumber;
            while(this.questionVersionNumber == this.prevQuestionVersionNumber){
                this.questionVersionNumber = rand.nextInt(currentQuestion.length()) + 1;
            }
            this.prevQuestionVersionNumber = this.questionVersionNumber;
            this.questionCode = String.format("%s%s%d", testId, questionId, questionVersionNumber);
            JSONObject currentQuestionVersion = currentQuestion.getJSONObject(questionVersionNumber - 1);
            JSONArray wrongAnswerJsonArray = currentQuestionVersion.getJSONArray("WrongAnswers");

            questionImageResource = currentQuestionVersion.getString("QuestionImage");
            questionText = currentQuestionVersion.getString("QuestionText");
            correctAnswer = currentQuestionVersion.getString("CorrectAnswer");
            explanation = currentQuestionVersion.getString("Explanation");
            difficulty = currentQuestionVersion.getInt("Difficulty");
            for (int i = 0; i < wrongAnswerJsonArray.length(); i++) {
                wrongAnswers[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswer");
                wrongAnswerClues[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswerClue");
            }
        } catch (JSONException jse){
            jse.printStackTrace();
            return;
        }
    }

    public void buildNewQuestion() {
        this.prevQuestionVersionNumber = -1;
        Random rand = new Random();
        //this.questionVersionNumber = questionVersionN;
        //this.questionCode = String.format("%s%s%d", testId, questionId, questionVersionNumber);
        //after these first two items are generated, grab the rest of the data from wherever it is stored.
        String jsonString = jsonFileHandler.loadJSONFromAsset();
        this.questionId = generateNextQuestionId();
        int newQuestionIndex = Integer.parseInt(this.questionId.substring(1)) - 1;
        try {
            JSONFromFile = new JSONObject(jsonString);
            JSONArray currentTest = JSONFromFile.getJSONArray(this.testId);
            JSONObject currentQuestionJsonObject = currentTest.getJSONObject(newQuestionIndex);
            while (currentQuestionJsonObject == null){
                this.questionId = generateNextQuestionId();
                newQuestionIndex = Integer.parseInt(this.questionId.substring(1)) - 1;
                currentQuestionJsonObject = currentTest.getJSONObject(newQuestionIndex);
            }
            JSONArray currentQuestion = currentQuestionJsonObject.getJSONArray(this.questionId);
            this.questionVersionNumber = rand.nextInt(currentQuestion.length()) + 1;
            this.questionCode = String.format("%s%s%d", testId, questionId, questionVersionNumber);
            JSONObject currentQuestionVersion = currentQuestion.getJSONObject(questionVersionNumber - 1);
            JSONArray wrongAnswerJsonArray = currentQuestionVersion.getJSONArray("WrongAnswers");

            questionImageResource = currentQuestionVersion.getString("QuestionImage");
            questionText = currentQuestionVersion.getString("QuestionText");
            correctAnswer = currentQuestionVersion.getString("CorrectAnswer");
            explanation = currentQuestionVersion.getString("Explanation");
            difficulty = currentQuestionVersion.getInt("Difficulty");
            for (int i = 0; i < wrongAnswerJsonArray.length(); i++) {
                wrongAnswers[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswer");
                wrongAnswerClues[i] = wrongAnswerJsonArray.getJSONObject(i).getString("WrongAnswerClue");
            }
        } catch (JSONException jse){
            jse.printStackTrace();
            return;
        }
    }

    public void shuffleWrongAnswers(int numberOfShuffles) {
        String temp = "";
        String tempClue = "";
        for(int i = 0; i < numberOfShuffles; i++) {
            Random rand = new Random();
            int swapItem1 = rand.nextInt(4);
            int swapItem2 = rand.nextInt(4);
            if(swapItem1 == swapItem2) {
                swapItem2 = (swapItem2 + 1) % 4;
            }
            temp = wrongAnswers[swapItem1];
            wrongAnswers[swapItem1] = wrongAnswers[swapItem2];
            wrongAnswers[swapItem2] = temp;

            tempClue = wrongAnswerClues[swapItem1];
            wrongAnswerClues[swapItem1] = wrongAnswerClues[swapItem2];
            wrongAnswerClues[swapItem2] = tempClue;
        }
    }

    public String generateNextQuestionId() {
        try {
            String nextQuestionId = "M";
            JSONArray currentTest = JSONFromFile.getJSONArray(this.testId);
            int nextQuestionIndex = (Integer.parseInt(questionId.substring(1))) % currentTest.length();
            int newQuestionNumber = nextQuestionIndex + 1;
            if(newQuestionNumber >= 10){
                nextQuestionId = nextQuestionId + newQuestionNumber;
            }
            else {
                nextQuestionId = nextQuestionId + "0" + newQuestionNumber;
            }
            return nextQuestionId;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}