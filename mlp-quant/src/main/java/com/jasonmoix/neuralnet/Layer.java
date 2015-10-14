package com.jasonmoix.neuralnet;

/**
 * Created by jmoix on 10/14/2015.
 */
public class Layer {

    public static final int OUTPUT_LAYER = 0;
    public static final int HIDDEN_LAYER = 1;

    private Node[] node;
    private int type;
    private int numWeights;

    public Layer(int hiddenNodes, int numInput, int type, double learningRate, double moment){
        this.type = type;
        this.numWeights = numInput;
        node = new Node[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            node[i] = new Node(numInput, learningRate, moment, i);
        }
    }

    public Layer(double[][] weights, int type, double learningRate, double moment){

        this.type = type;
        this.numWeights = weights[0].length;
        node = new Node[weights.length];
        for(int i = 0; i < weights.length; i++){
            node[i] = new Node(weights[i], learningRate, moment, i);
        }
    }

    public double[] getOutput(double inputValues[]){
        double[] output;

        if(type == HIDDEN_LAYER) {
            output = new double[node.length + 1];
            for (int i = 0; i < node.length; i++) {
                output[i] = node[i].getOutput(inputValues);
            }
            output[node.length] = 1;
        }else {
            output = new double[node.length];
            for (int i = 0; i < node.length; i++) {
                output[i] = node[i].getOutput(inputValues);
            }
        }

        return output;
    }

    public void adjustOutputWeights(double[][] hiddenOutput, double[][] predictedOutput, double[][] expectedOutput){
        for(int i = 0; i < node.length; i++){
            node[i].adjustOutputWeights(hiddenOutput, predictedOutput, expectedOutput);
        }
    }

    public void adjustInputWeights(double[][] inputValues, double[][] outputWeights, double[][] predictedOutput, double[][] expectedOutput){
        for(int i = 0; i < node.length; i++){
            node[i].adjustInputWeights(inputValues, outputWeights, predictedOutput, expectedOutput);
        }
    }

    public double[][] getWeights(){
        double[][] weights = new double[node.length][numWeights];

        for(int i = 0; i < node.length; i++){
            weights[i] = node[i].getWeights();
        }

        return weights;
    }

    public double getLearningRate(){
        return node[0].getLearningRate();
    }

    public double getMoment(){
        return node[0].getMoment();
    }

}
