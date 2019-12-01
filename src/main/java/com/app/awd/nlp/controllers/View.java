package com.app.awd.nlp.controllers;

import com.app.awd.nlp.TweetClassifier;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application {

    @FXML
    private Button testButton;

    @FXML
    private Label labelFileLearn;

    @FXML
    private Label labelFileTest;

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea resultArea;

//    @FXML
//    private Button checkButton;
//
//    @FXML
//    private Button learnButton;

    @FXML
    private TextArea trainingInfoArea;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Spinner<Integer> spinnerIterations;

    @FXML
    private Spinner<Integer> spinnerCutoff;

    private TweetClassifier tweetClassifier;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View.fxml"));
        primaryStage.setTitle("NLP Application");
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.show();
    }

    @FXML
    void handleLearnButtonClick() throws Exception{
        File recordsDir = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(recordsDir);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {

            tweetClassifier = new TweetClassifier(selectedFile.getAbsolutePath());
            labelFileLearn.setText("Learning file: " + selectedFile.getName());
            tweetClassifier.trainModel(choiceBox.getSelectionModel().getSelectedItem(), spinnerIterations.getValue(), spinnerCutoff.getValue());
            inputArea.setEditable(true);
            testButton.setDisable(false);
            labelFileTest.setText("");
            trainingInfoArea.setText("Model created!\nLoad test data or write on the area and check.");
        }
        else {
            labelFileLearn.setText("Load file one more time!");
            testButton.setDisable(false);
            inputArea.setEditable(false);
        }
    }

    @FXML
    void handleTestButtonClick() throws Exception {
        File recordsDir = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(recordsDir);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            trainingInfoArea.setText(tweetClassifier.testData(selectedFile.getAbsolutePath()));
            labelFileTest.setText("Test file: " + selectedFile.getName());
        }
        else
            labelFileTest.setText("Load file one more time!");
    }

    @FXML
    void handleCheckButtonClick()throws Exception{
        resultArea.setText(tweetClassifier.classifyNewTweet(inputArea.getText()));
    }



}
