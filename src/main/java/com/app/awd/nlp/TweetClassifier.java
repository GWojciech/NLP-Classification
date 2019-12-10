package com.app.awd.nlp;

import opennlp.tools.doccat.*;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.util.*;

import java.io.*;
import java.util.Scanner;

public class TweetClassifier {
    DoccatModel model;
    String nameOfFile;

    public TweetClassifier() {
        nameOfFile = "Files/tweetyRef.csv";
    }

    public TweetClassifier(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }

    public static void main(String[] args) {
        TweetClassifier twitterCategorizer = new TweetClassifier();
        twitterCategorizer.trainModel("NAIVEBAYES", 1, 0);
        String text = "";
        Scanner sc = new Scanner(System.in);
        while (!text.equals("stop")) {
            text = sc.nextLine();
            System.out.println(twitterCategorizer.classifyNewTweet(text));
        }
    }

    public void trainModel(String algorithm, Integer iterations, Integer cutoff) {
        try {

            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(nameOfFile));
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            // parametry treningu
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, iterations);
            params.put(TrainingParameters.CUTOFF_PARAM, cutoff+"");
            params.put(AbstractTrainer.ALGORITHM_PARAM, algorithm+"");

            model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            System.out.println("\nModel is successfully trained.");

            // zapis modelu
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream("model" + File.separator + "airline-classifier-naive-bayes.bin"));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : " + "model" + File.separator + "airline-classifier-naive-bayes.bin");

        } catch (IOException e) {
            System.out.println("An exception in reading the training file. Please check.");
            e.printStackTrace();
        }
    }


    //klasyfikacja nowego tweeta
    public String classifyNewTweet(String tweet) {
        DocumentCategorizer doccat = new DocumentCategorizerME(model);
        String [] docWords = tweet.split(" ");
        StringBuilder value;
        double[] aProbs = doccat.categorize(docWords);
        if (doccat.getBestCategory(aProbs).equals("0")) {
            value = new StringBuilder("The tweet is negative :(\n");
        } else if (doccat.getBestCategory(aProbs).equals("1")) {
            value = new StringBuilder("The tweet is neutral :|\n");
        } else {
            value = new StringBuilder("The tweet is positive :)\n");
        }
        value.append("Category : Probability\n");
        for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
            value.append(doccat.getCategory(i)).append(" : ").append(aProbs[i]).append("\n");
        }
        return value.toString();
    }

    //testowanie danych z pliku
    public String testData(String filePath) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
        DocumentCategorizer doccat = new DocumentCategorizerME(model);
        String row;
        int i = 0, correctClassifications = 0;
        double[] aProbs;
        String[] data, docWords;
        int [] correctClassClassification = new int[3];
        int [] allClassElements = new int[3];

        while ((row = csvReader.readLine()) != null) {
            i++;
            data = row.split("\t");
            docWords = data[1].split(" ");
            aProbs = doccat.categorize(docWords);
            if (doccat.getBestCategory(aProbs).equals(data[0])) {
                correctClassifications++;
                correctClassClassification[Integer.parseInt(data[0])]++;
            }
            allClassElements[Integer.parseInt(data[0])]++;
        }
        csvReader.close();
        return "Test file: \nCorrect classifications: " + correctClassifications + "/" + i +
                " (" +  String.format("%.2f", (double)correctClassifications/i * 100) + "%)\n" +
                "By class:\n"+
                "Negative(0): " + correctClassClassification[0] + "/" + allClassElements[0] + " (" +String.format("%.2f", (double)correctClassClassification[0]/allClassElements[0] * 100) + "%)\n"+
                "Neutral(1): " + correctClassClassification[1] + "/" + allClassElements[1] + " (" + String.format("%.2f", (double)correctClassClassification[1]/allClassElements[1] * 100) + "%)\n"+
                "Positive(2): " + correctClassClassification[2] + "/" + allClassElements[2] + " (" + String.format("%.2f", (double)correctClassClassification[2]/allClassElements[2] * 100) + "%)";
    }
}
