package com.jasonmoix.nfldata2;

import com.jasonmoix.neuralnet.mlpquant.MLP;
import com.jasonmoix.nfldata2.database.Database;
import com.jasonmoix.nfldata2.lineup_generator.LineupGenerator;

import java.io.File;

/**
 * Created by Souboro on 11/5/2015.
 */
public class Main {

    public static void main(String[] args){

        Database db = new Database();
        //db.initData();
        trainQBMLP(getInputData(db.getQBTrainingData()), getOutputData(db.getQBTrainingData()));
        trainRBMLP(getInputData(db.getRBTrainingData()), getOutputData(db.getRBTrainingData()));
        trainWRMLP(getInputData(db.getWRTrainingData()), getOutputData(db.getWRTrainingData()));
        trainTEMLP(getInputData(db.getTETrainingData()), getOutputData(db.getTETrainingData()));
        trainDSTMLP(getInputData(db.getDefenseTrainingData()), getOutputData(db.getDefenseTrainingData()));
        db.getPredictions();
        db.close();

        //LineupGenerator generator = new LineupGenerator();


    }

    public static void trainQBMLP(double[][] inputData, double[][] outputData){
        System.out.println("Training QB MLP...");
        int numInputs = inputData[0].length;
        int hiddenNodes = (numInputs < 2) ? 1 : numInputs/2;
        double learningRate = 0.000001;
        double moment = 0.9;
        double targetAccuracy = 5;
        File mlpOutput = new File("C:\\data\\mlps\\qb.txt");
        MLP qbMlp = new MLP(inputData, outputData, learningRate, moment, targetAccuracy, numInputs, hiddenNodes, mlpOutput, "QB");
    }

    public static void trainRBMLP(double[][] inputData, double[][] outputData){
        System.out.println("Training RB MLP...");
        int numInputs = inputData[0].length;
        int hiddenNodes = (numInputs < 2) ? 1 : numInputs/2;
        double learningRate = 0.000001;
        double moment = 0.9;
        double targetAccuracy = 5;
        File mlpOutput = new File("C:\\data\\mlps\\rb.txt");
        MLP rbMlp = new MLP(inputData, outputData, learningRate, moment, targetAccuracy, numInputs, hiddenNodes, mlpOutput, "RB");

    }

    public static void trainWRMLP(double[][] inputData, double[][] outputData){
        System.out.println("Training WR MLP...");
        int numInputs = inputData[0].length;
        int hiddenNodes = (numInputs < 2) ? 1 : numInputs/2;
        double learningRate = 0.000001;
        double moment = 0.9;
        double targetAccuracy = 5;
        File mlpOutput = new File("C:\\data\\mlps\\wr.txt");
        MLP wrMlp = new MLP(inputData, outputData, learningRate, moment, targetAccuracy, numInputs, hiddenNodes, mlpOutput, "WR");

    }

    public static void trainTEMLP(double[][] inputData, double[][] outputData){
        System.out.println("Training TE MLP...");
        int numInputs = inputData[0].length;
        int hiddenNodes = (numInputs < 2) ? 1 : numInputs/2;
        double learningRate = 0.000001;
        double moment = 0.9;
        double targetAccuracy = 5;
        File mlpOutput = new File("C:\\data\\mlps\\te.txt");
        MLP teMlp = new MLP(inputData, outputData, learningRate, moment, targetAccuracy, numInputs, hiddenNodes, mlpOutput, "TE");

    }

    public static void trainDSTMLP(double[][] inputData, double[][] outputData){
        System.out.println("Training DST MLP...");
        int numInputs = inputData[0].length;
        int hiddenNodes = (numInputs < 2) ? 1 : numInputs/2;
        double learningRate = 0.000001;
        double moment = 0.9;
        double targetAccuracy = 3;
        File mlpOutput = new File("C:\\data\\mlps\\dst.txt");
        MLP dstMlp = new MLP(inputData, outputData, learningRate, moment, targetAccuracy, numInputs, hiddenNodes, mlpOutput, "DST");

    }

    public static double[][] getInputData(double[][] trainingData){

        double[][] input = new double[trainingData.length][trainingData[0].length - 1];

        for(int i = 0; i < trainingData.length; i++){
            for(int j = 0; j < trainingData[0].length - 1; j++){
                input[i][j] = trainingData[i][j];
            }
        }

        return input;
    }

    public static double[][] getOutputData(double[][] trainingData){

        double[][] output = new double[trainingData.length][1];

        for(int i = 0; i < trainingData.length; i++){
            output[i][0] = trainingData[i][trainingData[0].length - 1];
        }

        return output;

    }

}
