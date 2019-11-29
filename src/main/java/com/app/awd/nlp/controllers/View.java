package com.app.awd.nlp.controllers;

import com.app.awd.nlp.NaiveBayesAlgorithm;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application {

    @FXML
    private Button testButton;

    @FXML
    private Label labelFile;

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea resultArea;

    @FXML
    private Button checkButton;

    private NaiveBayesAlgorithm naiveBayesAlgorithm ;

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
    void handleTestButtonClick() throws Exception{
        File recordsDir = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(recordsDir);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {

            naiveBayesAlgorithm = new NaiveBayesAlgorithm(selectedFile.getAbsolutePath());
            labelFile.setText("Learning file: " + selectedFile.getName());
            naiveBayesAlgorithm.trainModel();
            inputArea.setEditable(true);
        }
        else {
            labelFile.setText("Load file one more time!");
        }
    }

    @FXML
    void handleCheckButtonClick()throws Exception{
        resultArea.setText(naiveBayesAlgorithm.classifyNewTweet(inputArea.getText()));
    }


}
