package com.app.awd.nlp;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import opennlp.tools.doccat.*;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.util.*;

public class NaiveBayesAlgorithm {
    DoccatModel model;
    String nameOfFile;

    public NaiveBayesAlgorithm(){
        nameOfFile = "tweetyRef.csv";
    }

    public NaiveBayesAlgorithm(String nameOfFile){
        this.nameOfFile = nameOfFile;
    }

    public static void main(String[] args) {
        NaiveBayesAlgorithm twitterCategorizer = new NaiveBayesAlgorithm();
        twitterCategorizer.trainModel();
        String text = "";
        Scanner sc = new Scanner(System.in);
        while (!text.equals("stop")) {
            text = sc.nextLine();
            twitterCategorizer.classifyNewTweet(text);
        }
    }

    public void trainModel() {
        try {
            // read the training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(nameOfFile));
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            // define the training parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, 10);
            params.put(TrainingParameters.CUTOFF_PARAM, 0);
            params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);
//            params.put(AbstractTrainer.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE);

            // create a model from traning data
            model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            System.out.println("\nModel is successfully trained.");

            // save the model to local
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream("model" + File.separator + "airline-classifier-naive-bayes.bin"));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : " + "model" + File.separator + "airline-classifier-naive-bayes.bin");

            // test the model file by subjecting it to prediction
            DocumentCategorizer doccat = new DocumentCategorizerME(model);
            String[] docWords = "@USAirways No. Just felt that you could do better in making the emails feel a little less of “We don’t care. We’re automated.".replaceAll("[^A-Za-z]", " ").split(" ");
            System.out.println(Arrays.toString(docWords));
            double[] aProbs = doccat.categorize(docWords);

            // print the probabilities of the categories
            System.out.println("\n---------------------------------\nCategory : Probability\n---------------------------------");
            for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
                System.out.println(doccat.getCategory(i) + " : " + aProbs[i]);
            }
            System.out.println("---------------------------------");

            System.out.println("\n" + doccat.getBestCategory(aProbs) + " : is the predicted category for the given sentence.");
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
        } else if(doccat.getBestCategory(aProbs).equals("1")){
            return("The tweet is neutral :| ");
        }else{
            return("The tweet is positive :) ");

        }
    }
}
