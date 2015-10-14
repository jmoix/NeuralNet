package com.jasonmoix.neuralnet;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.*;

/**
 * Created by jmoix on 10/14/2015.
 */
public class MLP {

    public static final String NUM_HIDDEN_NODES = "numHiddenN";
    public static final String NUM_HIDDEN_WEIGHTS = "numHiddenW";
    public static final String NUM_OUTPUT_NODES = "numOutputN";
    public static final String NUM_OUTPUT_WEIGHTS = "numOutputW";
    public static final String HIDDEN_WEIGHTS = "hiddenWeights";
    public static final String OUTPUT_WEIGHTS = "outputWeights";
    public static final String LEARNING_RATE = "learningRate";
    public static final String MOMENT = "moment";

    private Layer hiddenLayer;
    private Layer outputLayer;
    private int hiddenNodes;
    private double targetAccuracy;
    private double inputValues[][];
    private double expectedValues[][];
    private double predictedValues[][];

    public MLP(
            double[][] inputValues,
            double[][] expectedValues,
            double learningRate,
            double moment,
            double targetAccuracy,
            int numInputs,
            int hiddenNodes){
        this.targetAccuracy = targetAccuracy;
        this.hiddenNodes = hiddenNodes;
        hiddenLayer = new Layer(hiddenNodes, numInputs + 1, Layer.HIDDEN_LAYER, learningRate, moment);
        outputLayer = new Layer(1, hiddenNodes + 1, Layer.OUTPUT_LAYER, learningRate, moment);
        train(inputValues, expectedValues);
    }

    public MLP(File inputFile){
        loadMLP(inputFile);
    }

    private void initInputValues(double[][] inputValues){
        this.inputValues = new double[inputValues.length][inputValues[0].length + 1];
        for(int i = 0; i < inputValues.length; i++){
            for(int j = 0; j < inputValues[0].length; j++){
                this.inputValues[i][j] = inputValues[i][j];
            }
            this.inputValues[i][inputValues[0].length] = 1;
        }
    }

    private void train(double[][] in,
                       double[][] exp){


        initInputValues(in);
        this.expectedValues = exp;
        predictedValues = new double[exp.length][exp[0].length];

        double MSE;
        int epoch = 0;

        while(true){

            double[][] hiddenOutput = new double[inputValues.length][hiddenNodes + 1];

            for(int i = 0; i < inputValues.length; i++){
                hiddenOutput[i] = hiddenLayer.getOutput(inputValues[i]);
            }

            for(int i = 0; i < inputValues.length; i++){
                predictedValues[i] = outputLayer.getOutput(hiddenOutput[i]);
            }

            double outputWeights[][] = outputLayer.getWeights();

            /*for(int i = 0; i < outputWeights.length; i++){
                System.out.println("Node " + (i+1) + " Weights:");
                for (int j = 0; j < outputWeights[0].length; j++){
                    System.out.println("    " + outputWeights[i][j]);
                }
            }*/

            outputLayer.adjustOutputWeights(hiddenOutput, predictedValues, expectedValues);

            hiddenLayer.adjustInputWeights(inputValues, outputWeights, predictedValues, expectedValues);

            //outputPredictedValues();
            MSE = calcMSE();
            System.out.println("MSE = " + MSE);
            if(MSE < targetAccuracy) break;

            epoch++;
        }

        outputPredictedValues(epoch);

    }

    private void outputPredictedValues(int epoch){
        System.out.println("Training finished at epoch " + epoch);
        for(int i = 0; i < predictedValues.length; i++){
            for(int j = 0; j < predictedValues[0].length; j++){
                System.out.print(predictedValues[i][j] + " ");
            }
            System.out.println();
        }
    }

    private double calcMSE(){

        double MSE = 0;

        for(int i = 0; i < predictedValues.length; i++){
            for(int j = 0; j < predictedValues[0].length; j++){
                MSE += (1.0/2.0)*Math.pow(expectedValues[i][j]-predictedValues[i][j], 2.0);
            }
        }

        MSE /= (predictedValues.length*predictedValues[0].length);

        return MSE;
    }

    public double predict(double[] inputValues){

        double[] prediction = outputLayer.getOutput(hiddenLayer.getOutput(inputValues));

        return prediction[0];
    }

    public void saveWeights(File output){

        try {
            FileWriter writer = new FileWriter(output);
            BufferedWriter bw = new BufferedWriter(writer);

            double[][] hiddenWeights = hiddenLayer.getWeights();
            double[][] outputWeights = outputLayer.getWeights();

            JsonArray hiddenWeightsJson = new JsonArray();

            for(int i = 0; i < hiddenWeights.length; i++){
                for(int j = 0; j < hiddenWeights[0].length; j++){
                    hiddenWeightsJson.add(hiddenWeights[i][j]);
                }
            }

            JsonArray outputWeightsJson = new JsonArray();

            for(int i = 0; i < outputWeights.length; i++){
                for(int j = 0; j < outputWeights[0].length; j++){
                    outputWeightsJson.add(outputWeights[i][j]);
                }
            }

            JsonObject weights = new JsonObject()
                    .put(LEARNING_RATE, hiddenLayer.getLearningRate())
                    .put(MOMENT, hiddenLayer.getMoment())
                    .put(NUM_HIDDEN_NODES, hiddenWeights.length)
                    .put(NUM_HIDDEN_WEIGHTS, hiddenWeights[0].length)
                    .put(NUM_OUTPUT_NODES, outputWeights.length)
                    .put(NUM_OUTPUT_WEIGHTS, outputWeights[0].length)
                    .put(HIDDEN_WEIGHTS, hiddenWeightsJson)
                    .put(OUTPUT_WEIGHTS, outputWeightsJson);

            //System.out.println(weights.encodePrettily());
            bw.write(weights.toString());

            bw.close();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadMLP(File input){

        try{

            FileReader reader = new FileReader(input);
            BufferedReader br = new BufferedReader(reader);

            JsonObject weights = new JsonObject(br.readLine());

            double[][] hiddenWeights = new double[weights.getInteger(NUM_HIDDEN_NODES)][weights.getInteger(NUM_HIDDEN_WEIGHTS)];
            double[][] outputWeights = new double[weights.getInteger(NUM_OUTPUT_NODES)][weights.getInteger(NUM_OUTPUT_WEIGHTS)];
            double learningRate = weights.getDouble(LEARNING_RATE);
            double moment = weights.getDouble(MOMENT);

            JsonArray hiddenJson = weights.getJsonArray(HIDDEN_WEIGHTS);
            JsonArray outputJson = weights.getJsonArray(OUTPUT_WEIGHTS);

            int jsonIndex = 0;
            for(int i = 0; i < hiddenWeights.length; i++){
                for(int j = 0; j < hiddenWeights[0].length; j++){
                    hiddenWeights[i][j] = hiddenJson.getDouble(jsonIndex);
                    jsonIndex++;
                }
            }

            jsonIndex = 0;
            for(int i = 0; i < outputWeights.length; i++){
                for(int j = 0; j < outputWeights[0].length; j++){
                    outputWeights[i][j] = outputJson.getDouble(jsonIndex);
                    jsonIndex++;
                }
            }

            hiddenLayer = new Layer(hiddenWeights, Layer.HIDDEN_LAYER, learningRate, moment);
            outputLayer = new Layer(outputWeights, Layer.OUTPUT_LAYER, learningRate, moment);
            //System.out.println(weights.encodePrettily());

            br.close();
            reader.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
