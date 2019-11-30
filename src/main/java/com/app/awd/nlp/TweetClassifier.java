package com.app.awd.nlp;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import opennlp.tools.doccat.*;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.util.*;

public class TweetClassifier {
    DoccatModel model;
    String nameOfFile;

    public TweetClassifier() {
        nameOfFile = "tweetyRef.csv";
    }

    public TweetClassifier(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }

//    public static void main(String[] args) {
//        NaiveBayesAlgorithm twitterCategorizer = new NaiveBayesAlgorithm();
//        twitterCategorizer.trainModel();
//        String text = "";
//        Scanner sc = new Scanner(System.in);
//        while (!text.equals("stop")) {
//            text = sc.nextLine();
//            twitterCategorizer.classifyNewTweet(text);
//        }
//    }

    public void trainModel(String algorithm, Integer iterations, Integer cutoff) {
        try {
            // read the training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(nameOfFile));
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            // define the training parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, iterations);
            params.put(TrainingParameters.CUTOFF_PARAM, cutoff+"");
            params.put(AbstractTrainer.ALGORITHM_PARAM, algorithm+"");
//            params.put(AbstractTrainer.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE);
            System.out.println(params.getParameters(""));
            // create a model from traning data
            model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            System.out.println("\nModel is successfully trained.");

            // save the model to local
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream("model" + File.separator + "airline-classifier-naive-bayes.bin"));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : " + "model" + File.separator + "airline-classifier-naive-bayes.bin");

        } catch (IOException e) {
            System.out.println("An exception in reading the training file. Please check.");
            e.printStackTrace();
        }
    }


    public String classifyNewTweet(String tweet) {
        DocumentCategorizer doccat = new DocumentCategorizerME(model);
//        String[] docWords = tweet.replaceAll("[^A-Za-z]", " ").split(" ");

        double[] aProbs = doccat.categorize(new String[]{tweet});
        System.out.println("\n---------------------------------\nCategory : Probability\n---------------------------------");
        for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
            System.out.println(doccat.getCategory(i) + " : " + aProbs[i]);
        }
        System.out.println("---------------------------------");
        if (doccat.getBestCategory(aProbs).equals("0")) {
            return ("The tweet is negative :( ");
        } else if (doccat.getBestCategory(aProbs).equals("1")) {
            return ("The tweet is neutral :| ");
        } else {
            return ("The tweet is positive :) ");

        }
    }

    public String testData(String filePath) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
        DocumentCategorizer doccat = new DocumentCategorizerME(model);
        String row;
        int i = 0, correctClassifications = 0;
        double[] aProbs;
        while ((row = csvReader.readLine()) != null) {
            i++;
            String[] data = row.split("\t");
//            System.out.println("["+ i + "]: " + data[0] + "->" + data[1]);
            aProbs = doccat.categorize(new String[]{data[1]});
            if (doccat.getBestCategory(aProbs).equals(data[0])) {
                correctClassifications++;
            }
        }
        csvReader.close();
        return "Test file: \nCorrect classifications: " + correctClassifications + " (" +  String.format("%.2f", (double)correctClassifications/i * 100) + "%)";
    }
}
